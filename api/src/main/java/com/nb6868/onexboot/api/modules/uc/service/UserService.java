package com.nb6868.onexboot.api.modules.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dao.UserDao;
import com.nb6868.onexboot.api.modules.uc.dto.ChangePasswordByMailCodeRequest;
import com.nb6868.onexboot.api.modules.uc.dto.RegisterRequest;
import com.nb6868.onexboot.api.modules.uc.dto.UserDTO;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.ChangeStateRequest;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.*;
import com.nb6868.onexboot.common.util.bcrypt.BCryptPasswordEncoder;
import com.nb6868.onexboot.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserService extends DtoService<UserDao, UserEntity, UserDTO> {

    @Autowired
    private ShiroService shiroService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private MailLogService mailLogService;

    @Override
    public QueryWrapper<UserEntity> getWrapper(String method, Map<String, Object> params) {
        QueryWrapper<UserEntity> qw = new WrapperUtils<UserEntity>(new QueryWrapper<>(), params)
                .eq("state", "uc_user.state")
                .eq("type", "uc_user.type")
                .like("username", "uc_user.username")
                .like("deptId", "uc_user.deptId")
                .like("mobile", "mobile")
                .like("realName", "real_name")
                .eq("tenantId", "tenant_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("real_name", search).or().like("username", search).like("mobile", search);
                })
                // 数据过滤
                .apply(Const.SQL_FILTER)
                .getQueryWrapper()
                .eq("uc_user.deleted", 0);

        // 普通管理员，只能查询所属部门及子部门的数据
        UserDetail user = SecurityUser.getUser();
        qw.in(user.getType() > UcConst.UserTypeEnum.SYSADMIN.value() && user.getDeptId() != null, "uc_user.dept_id", deptService.getSubDeptIdList(user.getDeptId()));
        // 角色
        String[] roleIds = ParamUtils.toArray(params, "roleIds");
        qw.and(roleIds.length > 0, queryWrapper -> {
            for (int i = 0; i < roleIds.length; i++) {
                queryWrapper.or(i != 0).apply("find_in_set({0}, role.roleIds)", roleIds[i]);
            }
        });
        return qw;
    }

    @Override
    protected void beforeSaveOrUpdateDto(UserDTO dto, UserEntity toSaveEntity, int type) {
        // 检查用户权限
        UserDetail user = SecurityUser.getUser();
        if (user.getType() > dto.getType()) {
            throw new OnexException("无权创建高等级用户");
        }
        // 系统管理员必须指定dept
        if (dto.getType() == UcConst.UserTypeEnum.DEPTADMIN.value() && dto.getDeptId() == null) {
            throw new OnexException("单位管理员需指定所在单位");
        }
        // 只能创建子部门
        if (user.getDeptId() != null) {
            if (dto.getDeptId() == null) {
                throw new OnexException("需指定所在单位");
            }
        }
        // 检查用户名和手机号是否已存在
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "username", dto.getUsername()), ErrorCode.ERROR_REQUEST, "用户名已存在");
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "mobile", dto.getMobile()), ErrorCode.ERROR_REQUEST, "手机号已存在");
        if (type == 1) {
            // 更新
            // 检查是否需要修改密码,对于null的不会更新字段
            toSaveEntity.setPassword(ObjectUtils.isEmpty(dto.getPassword()) ? null : PasswordUtils.encode(dto.getPassword()));
        } else {
            // 新增
            toSaveEntity.setPassword(PasswordUtils.encode(dto.getPassword()));
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, UserDTO dto, UserEntity existedEntity, int type) {
        if (ret) {
            // 保存角色用户关系
            roleUserService.saveOrUpdateByUserIdAndRoleIds(dto.getId(), dto.getRoleIdList());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicDeleteByIds(Collection<? extends Serializable> idList) {
        List<Long> ids = (List<Long>) idList;
        // 删除用户-角色关系
        roleUserService.deleteByUserIds(ids);
        // 删除用户token
        tokenService.deleteByUserIds(ids);
        return super.logicDeleteByIds(idList);
    }

    /**
     * 退出
     */
    public boolean logout(String token) {
        // 删除token
        return tokenService.deleteToken(token);
    }

    /**
     * 通过短信验证码修改密码
     */
    public Result<?> changePasswordBySmsCode(ChangePasswordByMailCodeRequest request) {
        OnexProps.LoginAdminProps loginAdminProps = shiroService.getLoginAdminProps();
        AssertUtils.isFalse(loginAdminProps.isForgetPassword(), "未开放修改密码功能");

        // 操作结果
        int resultCode = 0;
        // 登录用户
        UserEntity user = getOneByColumn("mobile", request.getMailTo());
        if (user == null) {
            // 帐号不存在
            resultCode = ErrorCode.ACCOUNT_NOT_EXIST;
        } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
            // 帐号锁定
            resultCode = ErrorCode.ACCOUNT_DISABLE;
        } else {
            //  校验验证码
            MailLogEntity lastSmsLog = mailLogService.findLastLogByTplCode(MsgConst.SMS_TPL_CHANGE_PASSWORD, user.getMobile());
            if (null == lastSmsLog || !request.getCode().equalsIgnoreCase(JacksonUtils.jsonToMap(lastSmsLog.getContentParams()).get("code").toString())) {
                // 验证码错误,找不到验证码
                resultCode = ErrorCode.SMS_CODE_ERROR;
            } else {
                // 验证码正确,校验有效时间
                if (DateUtils.timeDiff(lastSmsLog.getCreateTime()) > 15 * 60 * 1000) {
                    resultCode = ErrorCode.SMS_CODE_EXPIRED;
                } else {
                    // 验证成功,修改密码
                    updatePassword(user.getId(), request.getPassword());
                }
                // 将短信消费掉
                mailLogService.consumeById(lastSmsLog.getId());
            }
        }
        return new Result<>().setCode(resultCode);
    }

    /**
     * 注册
     */
    public Result<?> register(RegisterRequest request) {
        OnexProps.LoginAdminProps loginAdminProps = shiroService.getLoginAdminProps();
        AssertUtils.isFalse(loginAdminProps.isRegister(), "未开放注册");

        // 操作结果
        int resultCode = 0;
        // 登录用户
        if (isMobileExisted(request.getMobile(), null)) {
            return new Result<>().error(ErrorCode.HAS_DUPLICATED_RECORD, "手机号已注册");
        } else if (isUsernameExisted(request.getUsername(), null)) {
            return new Result<>().error(ErrorCode.HAS_DUPLICATED_RECORD, "用户名已注册");
        } else {
            //  校验验证码
            MailLogEntity lastSmsLog = mailLogService.findLastLogByTplCode(MsgConst.SMS_TPL_REGISTER, request.getMobile());
            if (null == lastSmsLog || !request.getSmsCode().equalsIgnoreCase(JacksonUtils.jsonToMap(lastSmsLog.getContentParams()).get("code").toString())) {
                // 验证码错误,找不到验证码
                resultCode = ErrorCode.SMS_CODE_ERROR;
            } else {
                // 验证码正确
                // 校验过期时间
                if (lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date())) {
                    resultCode = ErrorCode.SMS_CODE_EXPIRED;
                } else {
                    // 验证成功,创建用户
                    UserEntity entity = new UserEntity();
                    entity.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
                    entity.setUsername(request.getUsername());
                    entity.setMobile(request.getMobile());
                    entity.setMobileArea(request.getMobileArea());
                    save(entity);
                }
                // 将短信消费掉
                mailLogService.consumeById(lastSmsLog.getId());
            }
            return new Result<>().setCode(resultCode);
        }
    }

    /**
     * 通过手机号获取用户
     */
    public UserEntity getByMobile(String mobile) {
        return getByMobile("86", mobile);
    }

    /**
     * 通过手机号区域和手机号获取用户
     */
    public UserEntity getByMobile(String mobileArea, String mobile) {
        return query().eq("mobile_area", mobileArea).eq("mobile", mobile).last(Const.LIMIT_ONE).one();
    }

    /**
     * 修改状态
     */
    public boolean changeState(ChangeStateRequest request) {
        boolean ret = update().set("state", request.getState()).eq("id", request.getId()).update(new UserEntity());
        if (ret && request.getState() == Const.BooleanEnum.FALSE.value()) {
            // 停用将token注销
            tokenService.deleteByUserIds(Collections.singletonList(request.getId()));
        }
        return ret;
    }

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String newPassword) {
        return update().eq("id", id).set("password", PasswordUtils.encode(newPassword)).update(new UserEntity());
    }

    /**
     * 根据部门ID，查询用户数
     */
    public int getCountByDeptId(Long deptId) {
        return count(new QueryWrapper<UserEntity>().eq("dept_id", deptId));
    }

    /**
     * 判断用户名是否存在
     */
    public boolean isUsernameExisted(String username, Long id) {
        return hasDuplicated(id, "username", username);
    }

    /**
     * 判断手机号是否存在
     */
    public boolean isMobileExisted(String mobile, Long id) {
        return hasDuplicated(id, "mobile", mobile);
    }

    /**
     * 合并帐号,将mergeFrom数据合并到mergeTo
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean merge(String mergeTo, List<String> mergeFrom) {
        // 删除被合并帐号
        logicDeleteByIds(mergeFrom);
        // 将被删除业务数据中的create_id/update_id更新为mergeTo
        return true;
    }

}

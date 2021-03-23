package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dao.UserDao;
import com.nb6868.onexboot.api.modules.uc.dto.*;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.*;
import com.nb6868.onexboot.common.util.bcrypt.BCryptPasswordEncoder;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserServiceImpl extends CrudServiceImpl<UserDao, UserEntity, UserDTO> implements UserService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private MailLogService mailLogService;
    @Autowired
    private UserOauthService userOauthService;

    @Override
    public QueryWrapper<UserEntity> getWrapper(String method, Map<String, Object> params) {
        QueryWrapper<UserEntity> qw = new WrapperUtils<UserEntity>(new QueryWrapper<>(), params)
                .eq("status", "uc_user.status")
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
    public boolean logout(String token) {
        // 删除token
        return tokenService.deleteToken(token);
    }

    @Override
    public Kv login(LoginRequest loginRequest) {
        // 获得登录配置
        LoginTypeConfig loginTypeConfig = paramService.getCombineContentObject(UcConst.LOGIN_TYPE_PREFIX + loginRequest.getType(), LoginTypeConfig.class);
        AssertUtils.isNull(loginTypeConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 校验验证码
        if (loginTypeConfig.isCaptcha()) {
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.CaptchaGroup.class);
            boolean validateCaptcha = loginRequest.getCaptcha().equalsIgnoreCase(loginTypeConfig.getMagicCaptcha()) || captchaService.validate(loginRequest.getUuid(), loginRequest.getCaptcha());
            AssertUtils.isFalse(validateCaptcha, ErrorCode.CAPTCHA_ERROR);
        }

        // 登录用户
        UserEntity user;
        if (UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name().equalsIgnoreCase(loginRequest.getType()) || UcConst.LoginTypeEnum.APP_USER_PWD.name().equalsIgnoreCase(loginRequest.getType())) {
            // 帐号密码登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.UsernamePasswordGroup.class);
            user = getByUsername(loginRequest.getUsername());
            if (user == null) {
                // 帐号不存在
               throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                // 帐号锁定
                throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
            } else if (!PasswordUtils.matches(loginRequest.getPassword(), user.getPassword())) {
                // 密码不匹配
                throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
            }
        }  else if (UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name().equalsIgnoreCase(loginRequest.getType()) || UcConst.LoginTypeEnum.APP_MOBILE_SMS.name().equalsIgnoreCase(loginRequest.getType())) {
            // 手机号验证码登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.MobileSmsCodeGroup.class);
            user = getByMobile(loginRequest.getMobile());
            if (user == null) {
                // 帐号不存在
                throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                // 帐号锁定
                throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
            }
            //  校验验证码
            MailLogEntity lastSmsLog = mailLogService.findLastLogByTplCode(MsgConst.SMS_TPL_LOGIN, loginRequest.getMobile());
            if (null == lastSmsLog || !loginRequest.getSmsCode().equalsIgnoreCase(JacksonUtils.jsonToMap(lastSmsLog.getContentParams()).get("code").toString())) {
                // 验证码错误,找不到验证码
                throw new OnexException(ErrorCode.SMS_CODE_ERROR);
            } else {
                // 验证码正确
                // 校验过期时间
                if (lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date())) {
                    throw new OnexException(ErrorCode.SMS_CODE_EXPIRED);
                }
                // 将短信消费掉
                mailLogService.consumeById(lastSmsLog.getId());
            }
        } else if (UcConst.LoginTypeEnum.APP_APPLE.name().equalsIgnoreCase(loginRequest.getType())) {
            // 苹果登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.AppleGroup.class);
            // jwt解析identityToken, 获取userIdentifier
            DecodedJWT jwt = JWT.decode(loginRequest.getAppleIdentityToken());
            // app包名
            String packageName = jwt.getAudience().get(0);
            // 用户id
            String userIdentifier = jwt.getSubject();
            // 有效期
            Date expireTime = jwt.getExpiresAt();
            if (expireTime.after(new Date())) {
                throw new OnexException(ErrorCode.APPLE_LOGIN_ERROR);
            } else {
                // todo 使用apple keys做验证
                // {https://developer.apple.com/cn/app-store/review/guidelines/#sign-in-with-apple}
                // 通过packageName和userIdentifier找对应的数据记录
                UserOauthEntity userApple = userOauthService.getByOpenid(userIdentifier);
                if (userApple == null) {
                    // 不存在记录,则保存记录
                    userApple = new UserOauthEntity();
                    userApple.setAppid(packageName);
                    userApple.setOpenid(userIdentifier);
                    userApple.setType(UcConst.OauthTypeEnum.APPLE.name());
                    userOauthService.save(userApple);
                }
                if (userApple.getUserId() == null) {
                    // 未绑定用户
                    throw new OnexException(ErrorCode.APPLE_NOT_BIND);
                } else {
                    user = getById(userApple.getUserId());
                    if (user == null) {
                        // 帐号不存在
                        throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
                    } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                        // 帐号锁定
                        throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
                    }
                }
            }
        } else {
            throw new OnexException(ErrorCode.UNKNOWN_LOGIN_TYPE);
        }

        /*if (user == null && loginChannelCfg.isAutoCreate()) {
            // 没有该用户，并且需要自动创建用户
            user = new UserDTO();
            user.setState(UcConst.UserStateEnum.ENABLED.value());
            user.setMobile(loginRequest.getMobile());
            user.setUsername(loginRequest.getMobile());
            user.setType(UcConst.UserTypeEnum.USER.value());
            user.setGender(3);
            // 密码加密
            user.setPassword(PasswordUtils.encode(loginRequest.getMobile()));
            saveDto(user);
            //保存角色用户关系
            roleUserService.saveOrUpdate(user.getId(), user.getRoleIdList());
        }*/

        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginTypeConfig));
        kv.set("expire", loginTypeConfig.getExpire());
        kv.set("user", user);
        return kv;
    }

    @Override
    public Result<?> register(RegisterRequest request) {
        Map<String, Object> loginAdminConfig = paramService.getContentMap(UcConst.LOGIN_ADMIN);
        AssertUtils.isNull(loginAdminConfig, "未找到后台登录配置");

        AssertUtils.isFalse((boolean) loginAdminConfig.get("register"), "未开放注册");

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
                // 验证码正确,校验有效时间
                if (DateUtils.timeDiff(lastSmsLog.getCreateTime()) > 15 * 60 * 1000) {
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

    @Override
    public Result<?> changePasswordBySmsCode(ChangePasswordByMailCodeRequest request) {
        Map<String, Object> loginAdminConfig = paramService.getContentMap(UcConst.LOGIN_ADMIN);
        AssertUtils.isNull(loginAdminConfig, "未找到后台登录配置");

        AssertUtils.isFalse((boolean) loginAdminConfig.get("forgetPassword"), "未开放修改密码功能");

        // 操作结果
        int resultCode = 0;
        // 登录用户
        UserEntity user = getByMobile(request.getMailTo());
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

    @Override
    public UserEntity getByUsername(String username) {
        return query().eq("username", username).last(Const.LIMIT_ONE).one();
    }

    @Override
    public UserEntity getByMobile(String mobileArea, String mobile) {
        return query().eq("mobile_area", mobileArea).eq("mobile", mobile).last(Const.LIMIT_ONE).one();
    }

    @Override
    public UserEntity getByMobile(String mobile) {
        return getByMobile("86", mobile);
    }

    @Override
    public boolean changeState(UserDTO dto) {
        return update().set("status", dto.getState()).eq("id", dto.getId()).update(new UserEntity());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String newPassword) {
        return update().eq("id", id).set("password", PasswordUtils.encode(newPassword)).update(new UserEntity());
    }

    @Override
    public int getCountByDeptId(Long deptId) {
        return count(new QueryWrapper<UserEntity>().eq("dept_id", deptId));
    }

    @Override
    public boolean isUsernameExisted(String username, Long id) {
        return hasDuplicated(id, "username", username);
    }

    @Override
    public boolean isMobileExisted(String mobile, Long id) {
        return hasDuplicated(id, "mobile", mobile);
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

    /**
     * 合并帐号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean merge(String mergeTo, List<String> mergeFrom) {
        // 删除被合并帐号
        logicDeleteByIds(mergeFrom);
        // 将被删除业务数据中的create_id/update_id更新为mergeTo
        return true;
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

}

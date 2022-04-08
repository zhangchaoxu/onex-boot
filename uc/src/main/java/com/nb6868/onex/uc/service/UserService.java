package com.nb6868.onex.uc.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.ChangeStateForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.shiro.ShiroDao;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.UserDao;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.UserEntity;
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
    private ShiroDao shiroDao;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(ShiroUser user) {
        List<String> permissionsList = user.isFullPermissions() ? shiroDao.getAllPermissionsList(user.getTenantCode()) : shiroDao.getPermissionsListByUserId(user.getId());
        Set<String> set = new HashSet<>();
        permissionsList.forEach(permissions -> set.addAll(StrSplitter.splitTrim(permissions, ',', true)));
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(ShiroUser user) {
        List<String> roleList = user.isFullRoles() ? shiroDao.getAllRoleCodeList(user.getTenantCode()) : shiroDao.getRoleCodeListByUserId(user.getId());
        // 用户角色列表
        return new HashSet<>(roleList);
    }

    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoleUserService roleUserService;

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
        ShiroUser user = ShiroUtils.getUser();
        //qw.in(user.getType() > UcConst.UserTypeEnum.SYSADMIN.value() && user.getDeptId() != null, "uc_user.dept_id", deptService.getSubDeptIdList(user.getDeptId()));
        // 角色
        String[] roleIds = MapUtil.getStr(params, "roleIds", "").split(",");
        qw.and(roleIds.length > 0, queryWrapper -> {
            for (int i = 0; i < roleIds.length; i++) {
                queryWrapper.or(i != 0).apply("find_in_set({0}, role.role_ids)", roleIds[i]);
            }
        });
        return qw;
    }

    @Override
    protected void beforeSaveOrUpdateDto(UserDTO dto, UserEntity toSaveEntity, int type) {
        // 检查用户权限
        ShiroUser user = ShiroUtils.getUser();
        AssertUtils.isTrue(user.getType() > dto.getType(), "无权创建高等级用户");
        AssertUtils.isTrue(dto.getType() == UcConst.UserTypeEnum.DEPT_ADMIN.value() && StrUtil.isEmpty(dto.getDeptCode()), "单位管理员需指定所在单位");
        // AssertUtils.isTrue(user.getDeptId() != null && dto.getDeptId() == null, "需指定所在单位");
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "username", dto.getUsername()), ErrorCode.ERROR_REQUEST, "用户名已存在");
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "mobile", dto.getMobile()), ErrorCode.ERROR_REQUEST, "手机号已存在");
        if (type == 1) {
            // 更新
            // 检查是否需要修改密码,对于null的不会更新字段
            toSaveEntity.setPassword(ObjectUtils.isEmpty(dto.getPassword()) ? null : PasswordUtils.encode(dto.getPassword()));
            toSaveEntity.setPassword(ObjectUtils.isEmpty(dto.getPassword()) ? null : PasswordUtils.aesEncode(dto.getPassword(), Const.AES_KEY));
        } else {
            // 新增
            toSaveEntity.setPassword(PasswordUtils.encode(dto.getPassword()));
            toSaveEntity.setPasswordRaw(PasswordUtils.aesEncode(dto.getPassword(), Const.AES_KEY));
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, UserDTO dto, UserEntity existedEntity, int type) {
        if (ret) {
            // 保存角色用户关系
            roleUserService.saveOrUpdateByUserIdAndRoleCodes(dto.getId(), dto.getRoleCodes());
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
     * 修改状态
     */
    public boolean changeState(ChangeStateForm request) {
        boolean ret = update().set("state", request.getState()).eq("id", request.getId()).update(new UserEntity());
        if (ret && request.getState() == Const.BooleanEnum.FALSE.value()) {
            // 停用将token注销
            tokenService.deleteByUserIds(Collections.singletonList(request.getId()));
        }
        return ret;
    }

    /**
     * 合并帐号,将mergeFrom数据合并到mergeTo
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changeMenuScope(List<Long> menuIds) {
        // 保存用户菜单关系
        menuScopeService.saveOrUpdateByUserIdAndMenuIds(ShiroUtils.getUserId(), menuIds);
        return true;
    }

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String newPassword) {
        return update()
                .eq("id", id)
                .set("password", PasswordUtils.encode(newPassword))
                .set("password_raw", PasswordUtils.aesEncode(newPassword, Const.AES_KEY))
                .update(new UserEntity());
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

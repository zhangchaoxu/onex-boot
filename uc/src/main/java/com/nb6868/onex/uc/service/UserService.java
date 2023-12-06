package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.pojo.ChangeStateForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.shiro.ShiroDao;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.UserDao;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class UserService extends DtoService<UserDao, UserEntity, UserDTO> {

    @Autowired
    private ShiroDao shiroDao;
    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoleUserService roleUserService;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(ShiroUser user) {
        List<String> permissionsList = user.isFullPermissions() ? shiroDao.getAllPermissionsList(user.getTenantCode()) : shiroDao.getPermissionsListByUserId(user.getId());
        Set<String> set = new HashSet<>();
        permissionsList.forEach(permissions -> set.addAll(StrUtil.splitTrim(permissions, ',')));
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(ShiroUser user) {
        List<String> roleList = user.isFullRoles() ? shiroDao.getAllRoleIdList(user.getTenantCode()) : shiroDao.getRoleIdListByUserId(user.getId());
        return new HashSet<>(roleList);
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
            toSaveEntity.setPasswordRaw(ObjectUtils.isEmpty(dto.getPassword()) ? null : PasswordUtils.aesEncode(dto.getPassword(), Const.AES_KEY));
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
            roleUserService.saveOrUpdateByUserIdAndRoleIds(dto.getId(), dto.getRoleIds());
        }
    }

    /**
     * 通过用户名获取用户
     *
     * @param tenantCode 租户编码
     * @param username   用户名
     * @return 用户
     */
    public UserEntity getByUsername(String tenantCode, @NotNull String username) {
        return query().eq("username", username)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 通过用户名获取用户
     *
     * @param tenantCode 租户编码
     * @param mobile     手机号
     *
     * @return 用户
     */
    public UserEntity getByMobile(String tenantCode, @NotNull String mobile) {
        return query().eq("mobile", mobile)
                .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 删除数据本身及关联关系
     *
     * @param ids 角色id
     */
    public void deleteAllByIds(List<Long> ids) {
        // 删除用户本身
        removeByIds(ids);
        // 删除用户-角色关系
        roleUserService.deleteByUserIdList(ids);
        // 删除用户-权限关系
        menuScopeService.deleteByUserIdList(ids);
        // 删除用户token
        tokenService.deleteByUserIdList(ids);
    }

    /**
     * 修改状态
     */
    public boolean changeState(ChangeStateForm form) {
        boolean ret = update().set("state", form.getState()).eq("id", form.getId()).update(new UserEntity());
        if (ret) {
            if (form.getState() == UcConst.UserStateEnum.DISABLE.value()) {
                // 锁定用户,将token注销
                tokenService.deleteByUserIdList(Collections.singletonList(form.getId()));
            } else if (form.getState() == UcConst.UserStateEnum.ENABLED.value()) {
                // 激活用户
            }
        }
        return ret;
    }

    /**
     * 合并帐号,将mergeFrom数据合并到mergeTo
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean changeMenuScope(List<Long> menuIds) {
        // 保存用户菜单关系
        Long userId = ShiroUtils.getUserId();
        menuService.saveOrUpdateByUserIdAndMenuIds(userId, menuIds);
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
        removeByIds(mergeFrom);
        // 将被删除业务数据中的create_id/update_id更新为mergeTo
        return true;
    }

}

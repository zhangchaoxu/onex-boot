package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.RoleUserDao;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色用户关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RoleUserService extends EntityService<RoleUserDao, RoleUserEntity> {

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;

    /**
     * 更新用户角色关系
     *
     * @param userId  用户ID
     * @param roleIds 角色ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByUserIdAndRoleIds(Long userId, List<Long> roleIds, Integer type) {
        AssertUtils.isFalse(userService.hasIdRecord(userId), "用户ID不存在");
        if (CollUtil.isEmpty(roleIds)) {
            // 删除用户所有角色关系
            remove(lambdaQuery().eq(RoleUserEntity::getUserId, userId).eq(RoleUserEntity::getType, type).getWrapper());
        } else {
            // 判断传入的角色是否存在
            AssertUtils.isFalse(roleIds.size() == roleService.lambdaQuery()
                    .in(roleIds.size() > 1, RoleEntity::getId, roleIds)
                    .eq(roleIds.size() == 1, RoleEntity::getId, roleIds.get(0))
                    .count(), "请检查角色Id参数");
            roleIds.forEach(roleId -> {
                // 判断关系是否存在
                if (!lambdaQuery().eq(RoleUserEntity::getUserId, userId).eq(RoleUserEntity::getRoleId, roleId).eq(RoleUserEntity::getType, type).exists()) {
                    // 不存在的做保存
                    RoleUserEntity relEntity = new RoleUserEntity();
                    relEntity.setUserId(userId);
                    relEntity.setRoleId(roleId);
                    relEntity.setType(type);
                    save(relEntity);
                }
            });
            // 删除非指定范围内的其它的关系
            remove(lambdaQuery().eq(RoleUserEntity::getUserId, userId)
                    .notIn(roleIds.size() > 1, RoleUserEntity::getRoleId, roleIds)
                    .ne(roleIds.size() == 1, RoleUserEntity::getRoleId, roleIds.get(0))
                    .eq(RoleUserEntity::getType, type)
                    .getWrapper());
        }
        return true;
    }

    /**
     * 更新用户角色系
     *
     * @param roleId  角色ID
     * @param userIds 用户ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByRoleIdAndUserIds(Long roleId, List<Long> userIds, Integer type) {
        AssertUtils.isFalse(roleService.hasIdRecord(roleId), "角色ID不存在");
        if (CollUtil.isEmpty(userIds)) {
            // 删除部门所有该类型关系
            remove(lambdaQuery().eq(RoleUserEntity::getRoleId, roleId).eq(RoleUserEntity::getType, type).getWrapper());
        } else {
            // 判断传入的用户是否存在
            AssertUtils.isFalse(userIds.size() == userService.lambdaQuery()
                    .in(userIds.size() > 1, UserEntity::getId, userIds)
                    .eq(userIds.size() == 1, UserEntity::getId, userIds.get(0))
                    .count(), "请检查用户Id参数");
            userIds.forEach(userId -> {
                // 判断关系是否存在
                if (!lambdaQuery().eq(RoleUserEntity::getRoleId, roleId).eq(RoleUserEntity::getUserId, userId).eq(RoleUserEntity::getType, type).exists()) {
                    // 不存在的做保存
                    RoleUserEntity relEntity = new RoleUserEntity();
                    relEntity.setUserId(userId);
                    relEntity.setRoleId(roleId);
                    relEntity.setType(type);
                    save(relEntity);
                }
            });
            // 删除非指定范围内的其它的关系
            remove(lambdaQuery()
                    .eq(RoleUserEntity::getRoleId, roleId)
                    .notIn(userIds.size() > 1, RoleUserEntity::getUserId, userIds)
                    .ne(userIds.size() == 1, RoleUserEntity::getUserId, userIds.get(0))
                    .eq(RoleUserEntity::getType, type)
                    .getWrapper());
        }
        return true;
    }

    /**
     * 更新用户角色关系
     *
     * @param roleCode  角色编码
     * @param userIds 用户ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByRoleCodeAndUserIds(String roleCode, List<Long> userIds, Integer type) {
        RoleEntity role = roleService.getByCode(roleCode);
        AssertUtils.isNull(role, "角色编码不存在:" + roleCode);
        return updateByRoleIdAndUserIds(role.getId(), userIds, type);
    }

    /**
     * 根据角色ids，删除角色用户关系
     *
     * @param roleIds 角色ids
     */
    public boolean deleteByRoleIdList(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return true;
        }
        return remove(lambdaQuery()
                .in(roleIds.size() > 1, RoleUserEntity::getRoleId, roleIds)
                .eq(roleIds.size() == 1, RoleUserEntity::getRoleId, roleIds.get(0))
                .getWrapper());
    }

    /**
     * 根据用户id，删除角色用户关系
     *
     * @param userIds 用户ids
     */
    public boolean deleteByUserIdList(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return true;
        }
        return remove(lambdaQuery()
                .in(userIds.size() > 1, RoleUserEntity::getUserId, userIds)
                .eq(userIds.size() == 1, RoleUserEntity::getUserId, userIds.get(0))
                .getWrapper());
    }

}

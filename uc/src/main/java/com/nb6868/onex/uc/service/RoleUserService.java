package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.RoleUserDao;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
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

    /**
     * 更新用户角色关系，对角色做了检查，对用户没做检查
     *
     * @param userId  用户ID
     * @param roleIds 角色ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByUserIdAndRoleIds(Long userId, List<Long> roleIds, Integer type) {
        if (CollUtil.isEmpty(roleIds)) {
            // 删除用户所有角色关系
            remove(lambdaQuery().eq(RoleUserEntity::getUserId, userId).eq(RoleUserEntity::getType, type).getWrapper());
        } else {
            // 判断传入的角色是否存在
            AssertUtils.isFalse(roleIds.size() == roleService.lambdaQuery().in(RoleEntity::getId, roleIds).count(), "请检查角色Id参数");
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
            remove(lambdaQuery().eq(RoleUserEntity::getUserId, userId).notIn(RoleUserEntity::getRoleId, roleIds).eq(RoleUserEntity::getType, type).getWrapper());
        }
        return true;
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
        return remove(lambdaQuery().in(RoleUserEntity::getRoleId, roleIds).getWrapper());
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
        return remove(lambdaQuery().in(RoleUserEntity::getUserId, userIds).getWrapper());
    }

}

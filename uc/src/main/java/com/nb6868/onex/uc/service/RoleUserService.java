package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.dao.RoleUserDao;
import com.nb6868.onex.uc.entity.RoleUserEntity;
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

    /**
     * 保存或修改
     *
     * @param userId  用户ID
     * @param roleIds 角色ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateByUserIdAndRoleIds(Long userId, List<Long> roleIds) {
        // 先删除角色用户关系
        deleteByUserIdList(CollUtil.toList(userId));

        // 保存角色用户关系
        CollUtil.distinct(roleIds).forEach(roleId -> {
            RoleUserEntity roleUserEntity = new RoleUserEntity();
            roleUserEntity.setUserId(userId);
            roleUserEntity.setRoleId(roleId);
            save(roleUserEntity);
        });
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
        return logicDeleteByWrapper(update().in("role_id", roleIds));
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
        return logicDeleteByWrapper(update().in("user_id", userIds));
    }

}

package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.dao.RoleUserDao;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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
     * 保存或修改
     *
     * @param userId  用户ID
     * @param roleIds 角色ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateByUserIdAndRoleIds(Long userId, List<Long> roleIds) {
        // 先删除角色用户关系
        deleteByUserIds(Collections.singletonList(userId));

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
    public boolean deleteByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(new QueryWrapper<RoleUserEntity>().in("role_id", roleIds));
        }
    }

    /**
     * 根据用户id，删除角色用户关系
     *
     * @param userIds 用户ids
     */
    public boolean deleteByUserIds(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(new QueryWrapper<RoleUserEntity>().in("user_id", userIds));
        }
    }

}

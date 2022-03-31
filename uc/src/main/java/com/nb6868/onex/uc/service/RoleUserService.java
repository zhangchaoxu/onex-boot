package com.nb6868.onex.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.dao.RoleUserDao;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
     * @param roleCodes 角色编码列表
     */
    public boolean saveOrUpdateByUserIdAndRoleCodes(Long userId, List<String> roleCodes) {
        // 先删除角色用户关系
        deleteByUserIds(Collections.singletonList(userId));

        // 用户没有一个角色权限的情况
        if (CollectionUtils.isEmpty(roleCodes)) {
            return true;
        }
        roleCodes.forEach(roleCode -> {
            // 保存角色用户关系
            RoleUserEntity roleUserEntity = new RoleUserEntity();
            roleUserEntity.setUserId(userId);
            roleUserEntity.setRoleCode(roleCode);
            save(roleUserEntity);
        });
        return true;
    }

    /**
     * 根据角色ids，删除角色用户关系
     *
     * @param roleCodes 角色ids
     */
    public boolean deleteByRoleCodes(List<String> roleCodes) {
        if (CollectionUtils.isEmpty(roleCodes)) {
            return true;
        } else {
            return logicDeleteByWrapper(new QueryWrapper<RoleUserEntity>().in("role_code", roleCodes));
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

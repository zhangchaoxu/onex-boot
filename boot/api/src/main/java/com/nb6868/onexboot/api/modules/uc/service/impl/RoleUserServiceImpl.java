package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.nb6868.onexboot.api.modules.uc.dao.RoleUserDao;
import com.nb6868.onexboot.common.service.impl.BaseServiceImpl;
import com.nb6868.onexboot.api.modules.uc.entity.RoleUserEntity;
import com.nb6868.onexboot.api.modules.uc.service.RoleUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 角色用户关系
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class RoleUserServiceImpl extends BaseServiceImpl<RoleUserDao, RoleUserEntity> implements RoleUserService {

    @Override
    public boolean saveOrUpdate(Long userId, List<Long> roleIds) {
        // 先删除角色用户关系
        deleteByUserIds(Collections.singletonList(userId));

        // 用户没有一个角色权限的情况
        if(CollectionUtils.isEmpty(roleIds)){
            return false;
        }

        // 保存角色用户关系
        for(Long roleId : roleIds){
            RoleUserEntity sysRoleUserEntity = new RoleUserEntity();
            sysRoleUserEntity.setUserId(userId);
            sysRoleUserEntity.setRoleId(roleId);

            // 保存
            save(sysRoleUserEntity);
        }
        return true;
    }

    @Override
    public boolean deleteByRoleIds(List<Long> roleIds) {
        return logicDeleteByWrapper(new QueryWrapper<RoleUserEntity>().in("role_id", roleIds));
    }

    @Override
    public boolean deleteByUserIds(List<Long> userIds) {
        return logicDeleteByWrapper(new QueryWrapper<RoleUserEntity>().in("user_id", userIds));
    }

    @Override
    public List<Long> getRoleIdList(Long userId) {
        return listObjs(new QueryWrapper<RoleUserEntity>().select("role_id").eq("user_id", userId), o -> Long.valueOf(String.valueOf(o)));
    }

}

package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.nb6868.onexboot.api.modules.uc.dao.RoleMenuDao;
import com.nb6868.onexboot.common.service.impl.BaseServiceImpl;
import com.nb6868.onexboot.api.modules.uc.entity.RoleMenuEntity;
import com.nb6868.onexboot.api.modules.uc.service.RoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 角色与菜单对应关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RoleMenuServiceImpl extends BaseServiceImpl<RoleMenuDao, RoleMenuEntity> implements RoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String roleId, List<Long> menuIds) {
        // 先删除角色菜单关系
        deleteByRoleIds(Collections.singletonList(roleId));

        if (ObjectUtils.isNotEmpty(menuIds)) {
            //保存角色菜单关系
            for (Long menuId : menuIds) {
                RoleMenuEntity roleMenu = new RoleMenuEntity();
                roleMenu.setMenuId(menuId);
                roleMenu.setRoleId(roleId);
                //保存
                save(roleMenu);
            }
        }
    }

    @Override
    public List<Long> getMenuIdListByRoleId(String roleId) {
        return listObjs(new QueryWrapper<RoleMenuEntity>().select("menu_id").eq("role_id", roleId),  o -> Long.valueOf(String.valueOf(o)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleIds(List<String> roleIds) {
        return logicDeleteByWrapper(new QueryWrapper<RoleMenuEntity>().in("role_id", roleIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        return logicDeleteByWrapper(new QueryWrapper<RoleMenuEntity>().in("menu_id", menuIds));
    }

}

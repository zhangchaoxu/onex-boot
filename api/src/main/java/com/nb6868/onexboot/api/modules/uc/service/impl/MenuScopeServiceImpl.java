package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.nb6868.onexboot.api.modules.uc.dao.MenuScopeDao;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.service.MenuService;
import com.nb6868.onexboot.common.service.impl.BaseServiceImpl;
import com.nb6868.onexboot.api.modules.uc.entity.MenuScopeEntity;
import com.nb6868.onexboot.api.modules.uc.service.MenuScopeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MenuScopeServiceImpl extends BaseServiceImpl<MenuScopeDao, MenuScopeEntity> implements MenuScopeService {

    @Autowired
    private MenuService menuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByRoleAndMenuIds(Long roleId, String roleName, List<Long> menuIds) {
        // 先删除角色菜单关系
        deleteByRoleIds(Collections.singletonList(roleId));

        if (ObjectUtils.isNotEmpty(menuIds)) {
            List<MenuEntity> menus = menuService.listByIds(menuIds);
            //保存角色菜单关系
            for (MenuEntity menu : menus) {
                MenuScopeEntity menuScope = new MenuScopeEntity();
                menuScope.setType(1);
                menuScope.setMenuId(menu.getId());
                menuScope.setMenuPermissions(menu.getPermissions());
                menuScope.setRoleId(roleId);
                menuScope.setRoleName(roleName);
                //保存
                save(menuScope);
            }
        }
    }

    @Override
    public List<Long> getMenuIdListByRoleId(Long roleId) {
        return listObjs(new QueryWrapper<MenuScopeEntity>().select("menu_id").eq("role_id", roleId), o -> Long.valueOf(String.valueOf(o)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleIds(List<Long> roleIds) {
        return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("role_id", roleIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("menu_id", menuIds));
    }

}

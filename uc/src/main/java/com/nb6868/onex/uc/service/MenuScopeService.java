package com.nb6868.onex.uc.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.MenuScopeDao;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
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
public class MenuScopeService extends EntityService<MenuScopeDao, MenuScopeEntity> {

    @Autowired
    private MenuService menuService;

    /**
     * 保存角色和菜单的关系
     *
     * @param roleId   角色ID
     * @param roleName 角色名
     * @param menuIds  菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByRoleAndMenuIds(Long roleId, String roleName, List<Long> menuIds) {
        // 先删除角色菜单关系
        deleteByRoleIds(Collections.singletonList(roleId));

        if (ObjectUtil.isNotEmpty(menuIds)) {
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

    /**
     * 保存用户和菜单的关系
     *
     * @param userId   用户ID
     * @param menuIds  菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByUserIdAndMenuIds(Long userId, List<Long> menuIds) {
        // 先删除角色菜单关系
        deleteByUserId(userId);

        if (ObjectUtil.isNotEmpty(menuIds)) {
            List<MenuEntity> menus = menuService.listByIds(menuIds);
            //保存角色菜单关系
            for (MenuEntity menu : menus) {
                MenuScopeEntity menuScope = new MenuScopeEntity();
                menuScope.setType(2);
                menuScope.setUserId(userId);
                menuScope.setMenuId(menu.getId());
                menuScope.setMenuPermissions(menu.getPermissions());
                //保存
                save(menuScope);
            }
        }
    }

    /**
     * 通过用户ID获得所有权限
     */
    public List<String> getPermissionsListByUserId(Long userId) {
        return getBaseMapper().getPermissionsListByUserId(userId);
    }

    /**
     * 通过用户ID获得所有菜单ID
     */
    public List<Long> getMenuIdListByUserId(Long userId) {
        return getBaseMapper().getMenuIdListByUserId(userId);
    }

    /**
     * 根据角色ID，获取菜单ID列表
     */
    public List<Long> getMenuIdListByRoleId(Long roleId) {
        return listObjs(new QueryWrapper<MenuScopeEntity>().select("menu_id").eq("role_id", roleId), o -> Long.valueOf(String.valueOf(o)));
    }

    /**
     * 根据用户id，删除用户菜单关系
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().eq("user_id", userId).eq("type", UcConst.MenuScopeTypeEnum.USER.value()));
    }

    /**
     * 根据角色id，删除角色菜单关系
     *
     * @param roleCodes 角色编码
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleCodes(List<String> roleCodes) {
        return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("role_id", roleCodes).eq("type", UcConst.MenuScopeTypeEnum.ROLE.value()));
    }

    /**
     * 根据菜单id，删除角色菜单关系
     *
     * @param menuIds 菜单ids
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("menu_id", menuIds));
    }

}

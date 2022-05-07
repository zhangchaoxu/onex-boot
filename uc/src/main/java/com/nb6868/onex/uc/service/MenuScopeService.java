package com.nb6868.onex.uc.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.MenuScopeDao;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
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
     * @param roleId 角色ID
     * @param menuIds  菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByRoleIdAndMenuIds(Long roleId, List<Long> menuIds) {
        // 先删除角色菜单关系
        deleteByRoleIdList(Collections.singletonList(roleId));

        if (ObjectUtil.isNotEmpty(menuIds)) {
            menuService.listByIds(menuIds).forEach(menu -> {
                //保存角色菜单关系
                MenuScopeEntity menuScope = new MenuScopeEntity();
                menuScope.setType(UcConst.MenuScopeTypeEnum.ROLE.value());
                menuScope.setMenuId(menu.getId());
                menuScope.setMenuPermissions(menu.getPermissions());
                menuScope.setRoleId(roleId);
                save(menuScope);
            });
        }
    }

    /**
     * 保存用户和菜单的关系
     *
     * @param userId  用户ID
     * @param menuIds 菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByUserIdAndMenuIds(Long userId, List<Long> menuIds) {
        // 先删除用户菜单关系
        deleteByUserIdList(Collections.singletonList(userId));

        if (ObjectUtil.isNotEmpty(menuIds)) {
            //保存用户菜单关系
            menuService.listByIds(menuIds).forEach(menu -> {
                MenuScopeEntity menuScope = new MenuScopeEntity();
                menuScope.setType(UcConst.MenuScopeTypeEnum.USER.value());
                menuScope.setMenuId(menu.getId());
                menuScope.setMenuPermissions(menu.getPermissions());
                menuScope.setUserId(userId);
                save(menuScope);
            });
        }
    }

    /**
     * 根据角色编码，获取菜单ID列表
     *
     * @param roleId 角色ID
     */
    public List<Long> getMenuIdListByRoleId(@NotNull Long roleId) {
        return listObjs(new QueryWrapper<MenuScopeEntity>()
                .select("menu_id")
                .eq("role_id", roleId), o -> Long.valueOf(String.valueOf(o)));
    }

    /**
     * 根据用户id，删除用户菜单关系
     *
     * @param userIds 用户ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserIdList(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("user_id", userIds).eq("type", UcConst.MenuScopeTypeEnum.USER.value()));
        }
    }

    /**
     * 根据角色id，删除角色菜单关系
     *
     * @param roleIds 角色ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleIdList(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("role_id", roleIds).eq("type", UcConst.MenuScopeTypeEnum.ROLE.value()));
        }
    }

    /**
     * 根据菜单id，删除角色菜单关系
     *
     * @param menuIds 菜单ids
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(new QueryWrapper<MenuScopeEntity>().in("menu_id", menuIds));
        }
    }

}

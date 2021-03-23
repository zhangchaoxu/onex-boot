package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.api.modules.uc.entity.MenuScopeEntity;
import com.nb6868.onexboot.common.service.BaseService;

import java.util.List;

/**
 * 角色与菜单对应关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MenuScopeService extends BaseService<MenuScopeEntity> {

    /**
     * 通过用户ID获得所有权限
     */
    List<String> getPermissionsListByUserId(Long userId);

    /**
     * 通过用户ID获得所有菜单ID
     */
    List<Long> getMenuIdListByUserId(Long userId);

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> getMenuIdListByRoleId(Long roleId);

    /**
     * 保存角色和菜单的关系
     *
     * @param roleId       角色ID
     * @param roleName       角色名
     * @param menuIdList 菜单ID列表
     */
    void saveOrUpdateByRoleAndMenuIds(Long roleId, String roleName, List<Long> menuIdList);

    /**
     * 根据角色id，删除角色菜单关系
     *
     * @param roleIds 角色ids
     */
    boolean deleteByRoleIds(List<Long> roleIds);

    /**
     * 根据菜单id，删除角色菜单关系
     *
     * @param menuIds 菜单ids
     */
    boolean deleteByMenuIds(List<Long> menuIds);

}

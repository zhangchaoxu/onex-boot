package com.nb6868.onex.modules.uc.service;

import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.uc.dto.MenuDTO;
import com.nb6868.onex.modules.uc.dto.MenuTreeDTO;
import com.nb6868.onex.modules.uc.entity.MenuEntity;
import com.nb6868.onex.modules.uc.user.UserDetail;

import java.util.List;

/**
 * 菜单管理
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public interface MenuService extends CrudService<MenuEntity, MenuDTO> {

    /**
     * 菜单列表
     *
     * @param type 菜单类型
     */
    List<MenuTreeDTO> getTreeByType(Integer type);

    /**
     * 用户菜单列表
     *
     * @param user 用户
     * @param type 菜单类型
     */
    List<MenuTreeDTO> getTreeByUser(UserDetail user, Integer type);

    /**
     * 递归上级菜单列表
     *
     * @param id 菜单ID
     */
    List<MenuDTO> getParentList(Long id);

    /**
     * 通过type获取菜单列表
     *
     * @param type 类型
     * @return result
     */
    List<MenuEntity> getListByType(Integer type);

    /**
     * 用户Url列表
     *
     * @param user 用户
     */
    List<MenuEntity> getListByUser(UserDetail user, Integer type);

    /**
     * 查询角色权限列表
     *
     * @param roleCodes 角色列表
     * @return result
     */
    List<String> getPermissionsListByRoles(List<String> roleCodes);

    /**
     * 查询所有权限列表
     *
     * @return result
     */
    List<String> getPermissionsList();

    /**
     * 查询用户权限列表
     *
     * @param userId 用户ID
     * @return result
     */
    List<String> getPermissionsListByUserId(Long userId);

    /**
     * 查询所有级联的子节点id
     */
    List<Long> getCascadeChildrenListByIds(List<Long> ids);
}

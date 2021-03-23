package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.uc.dto.MenuDTO;
import com.nb6868.onexboot.api.modules.uc.dto.MenuTreeDTO;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;

import java.util.List;

/**
 * 菜单管理
 *
 * @author Charles zhangchaoxu@gmail.com
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
     * 查询所有权限列表
     *
     * @return result
     */
    List<String> getPermissionsList();

    /**
     * 查询所有级联的子节点id
     */
    List<Long> getCascadeChildrenListByIds(List<Long> ids);
}

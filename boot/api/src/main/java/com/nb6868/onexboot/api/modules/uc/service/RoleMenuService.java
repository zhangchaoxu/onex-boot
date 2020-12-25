package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.common.service.BaseService;
import com.nb6868.onexboot.api.modules.uc.entity.RoleMenuEntity;

import java.util.List;


/**
 * 角色与菜单对应关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface RoleMenuService extends BaseService<RoleMenuEntity> {

	/**
	 * 根据角色ID，获取菜单ID列表
	 */
	List<Long> getMenuIdListByRoleId(Long roleId);

	/**
	 * 保存或修改
	 * @param roleId      角色ID
	 * @param menuIdList  菜单ID列表
	 */
	void saveOrUpdate(Long roleId, String roleCode, List<Long> menuIdList);

	/**
	 * 根据角色id，删除角色菜单关系
	 * @param roleIds 角色ids
	 */
	boolean deleteByRoleIds(List<Long> roleIds);

	/**
	 * 根据菜单id，删除角色菜单关系
	 * @param menuIds 菜单ids
	 */
	boolean deleteByMenuIds(List<Long> menuIds);
}

package com.nb6868.onex.modules.uc.service;

import com.nb6868.onex.booster.service.BaseService;
import com.nb6868.onex.modules.uc.entity.RoleDataScopeEntity;

import java.util.List;

/**
 * 角色数据权限
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public interface RoleDataScopeService extends BaseService<RoleDataScopeEntity> {

    /**
     * 保存或修改
     * @param roleId      角色ID
     * @param deptIdList  部门ID列表
     */
    void saveOrUpdate(Long roleId, List<Long> deptIdList);

    /**
     * 根据角色id，删除角色数据权限关系
     * @param roleIds 角色ids
     */
    boolean deleteByRoleIds(List<Long> roleIds);

    /**
     * 获取用户的部门数据权限列表
     */
    List<Long> getDeptIdListByUserId(Long userId);

    /**
     * 根据角色ID，获取部门ID列表
     */
    List<Long> getDeptIdListByRoleId(Long userId);
}

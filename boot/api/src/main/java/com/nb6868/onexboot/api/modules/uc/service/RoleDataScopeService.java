package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.common.service.BaseService;
import com.nb6868.onexboot.api.modules.uc.entity.RoleDataScopeEntity;

import java.util.List;

/**
 * 角色数据权限
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface RoleDataScopeService extends BaseService<RoleDataScopeEntity> {

    /**
     * 保存或修改
     * @param roleId      角色ID
     * @param deptIdList  部门ID列表
     */
    void saveOrUpdate(String roleId, List<Long> deptIdList);

    /**
     * 根据角色id，删除角色数据权限关系
     * @param roleIds 角色ids
     */
    boolean deleteByRoleIds(List<String> roleIds);

    /**
     * 获取用户的部门数据权限列表
     */
    List<Long> getDeptIdListByUserId(Long userId);

    /**
     * 根据角色ID，获取部门ID列表
     */
    List<Long> getDeptIdListByRoleId(String roleId);
}

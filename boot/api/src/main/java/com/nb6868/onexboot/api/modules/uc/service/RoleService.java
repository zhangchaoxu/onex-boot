package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.api.modules.uc.dto.RoleDTO;
import com.nb6868.onexboot.common.service.CrudService;
import com.nb6868.onexboot.api.modules.uc.entity.RoleEntity;

import java.util.List;

/**
 * 角色
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public interface RoleService extends CrudService<RoleEntity, RoleDTO> {

    /**
     * 查询所有角色列表
     */
    List<String> getRoleCodeList();

    /**
     * 根据用户查询角色列表
     *
     * @param userId 用户id
     */
    List<String> getRoleCodeListByUserId(Long userId);

}

package com.nb6868.onex.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.uc.dao.RoleDao;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RoleService extends DtoService<RoleDao, RoleEntity, RoleDTO> {

    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private RoleUserService roleUserService;

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, RoleDTO dto, RoleEntity existedEntity, int type) {
        if (ret) {
            // 保存角色菜单关系
            menuScopeService.saveOrUpdateByRoleAndMenuIds(dto.getCode(), dto.getMenuIdList());
            if (1 == type ) {
                // 如果是更新,则更新角色用户表中的角色编码字段
                roleUserService.update().set("role_code", dto.getCode()).eq("role_code", existedEntity.getCode()).update(new RoleUserEntity());
            }
        }
    }

    /**
     * 查询所有角色列表
     */
    public List<String> getRoleCodeList() {
        return listObjs(new QueryWrapper<RoleEntity>().select("id"), String::valueOf);
    }

    /**
     * 根据用户查询角色列表
     *
     * @param userId 用户id
     */
    public List<String> getRoleCodeListByUserId(Long userId) {
        return roleUserService.listObjs(new QueryWrapper<RoleUserEntity>().select("role_code").eq("user_id", userId), String::valueOf);
    }

}

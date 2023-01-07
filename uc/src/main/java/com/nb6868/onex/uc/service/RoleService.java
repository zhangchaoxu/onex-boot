package com.nb6868.onex.uc.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.uc.dao.RoleDao;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    private MenuService menuService;
    @Autowired
    private RoleUserService roleUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void afterSaveOrUpdateDto(boolean ret, RoleDTO dto, RoleEntity existedEntity, int type) {
        if (ret) {
            // 删除角色菜单关系
            menuService.saveOrUpdateByRoleIdAndMenuIds(dto.getId(), dto.getMenuIdList());
        }
    }

    /**
     * 根据用户查询角色列表
     *
     * @param userId 用户id
     */
    public List<Long> getRoleIdListByUserId(Long userId) {
        return roleUserService.listObjs(new QueryWrapper<RoleUserEntity>().select("role_id").eq("user_id", userId).groupBy("role_id"), o -> Long.valueOf(String.valueOf(o)));
    }

    /**
     * 根据角色id查询用户列表
     *
     * @param roleIds 角色id
     */
    public List<Long> getUserIdListByRoleIdList(List<Long> roleIds) {
        if (ObjectUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        } else {
            return roleUserService.listObjs(new QueryWrapper<RoleUserEntity>().select("user_id").in("role_id", roleIds).groupBy("user_id"), o -> Long.valueOf(String.valueOf(o)));
        }
    }

    /**
     * 删除数据本身及关联关系
     * @param id 角色id
     */
    public void deleteAllById(Long id) {
        // 删除角色
        removeById(id);
        // 删除角色菜单关联关系
        menuScopeService.deleteByRoleIdList(Collections.singletonList(id));
        // 删除角色用户关联关系
        roleUserService.deleteByRoleIdList(Collections.singletonList(id));
    }

}

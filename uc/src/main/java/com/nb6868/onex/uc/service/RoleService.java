package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.RoleDao;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
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
    MenuScopeService menuScopeService;
    @Autowired
    MenuService menuService;
    @Autowired
    RoleUserService roleUserService;

    @Override
    protected void beforeSaveOrUpdateDto(RoleDTO dto, int type) {
        // 新增，检查角色编码是否存在
        boolean hasRecord = hasDuplicated(dto.getId(), "code", dto.getCode());
        AssertUtils.isTrue(hasRecord, "编码[" + dto.getId() + "]已存在");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterSaveOrUpdateDto(boolean ret, RoleDTO dto, RoleEntity existedEntity, int type) {
        if (ret) {
            // 重新保存角色和菜单关系表
            menuService.saveOrUpdateByRoleIdAndMenuIds(dto.getId(), dto.getMenuIdList());
        }
    }

    /**
     * 根据用户查询角色ID列表
     *
     * @param userId 用户id
     */
    public List<Long> getRoleIdListByUserId(@NotNull Long userId) {
        return CollStreamUtil.toList(roleUserService.lambdaQuery()
                .select(RoleUserEntity::getRoleId)
                .eq(RoleUserEntity::getUserId, userId)
                .groupBy(RoleUserEntity::getRoleId)
                .list(), RoleUserEntity::getRoleId);
    }

    /**
     * 根据角色ID查询用户列表
     *
     * @param roleIds 角色id
     */
    public List<Long> getUserIdListByRoleIdList(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(roleUserService.lambdaQuery()
                .select(RoleUserEntity::getUserId)
                .in(RoleUserEntity::getRoleId, roleIds)
                .groupBy(RoleUserEntity::getUserId)
                .list(), RoleUserEntity::getUserId);
    }

    /**
     * 根据角色编码查询角色ID数组
     *
     * @param roleCodes 角色编码
     */
    public List<Long> getRoleIdListByRoleCodeList(List<String> roleCodes) {
        if (CollUtil.isEmpty(roleCodes)) {
            return CollUtil.newArrayList();
        }
        return CollStreamUtil.toList(lambdaQuery()
                .select(RoleEntity::getId)
                .in(RoleEntity::getCode, roleCodes)
                .list(), RoleEntity::getId);
    }

    /**
     * 根据角色编码查询用户列表
     *
     * @param roleCodes 角色编码
     */
    public List<Long> getUserIdListByRoleCodeList(List<String> roleCodes) {
        if (CollUtil.isEmpty(roleCodes)) {
            return CollUtil.newArrayList();
        }
        // 先找到角色id
        List<Long> roleIds = getRoleIdListByRoleCodeList(roleCodes);
        // 在用角色ID找到用户id
        return getUserIdListByRoleIdList(roleIds);
    }

    /**
     * 删除数据本身及关联关系
     *
     * @param id 角色id
     */
    public void deleteAllById(@NotNull Long id) {
        // 删除角色
        removeById(id);
        // 删除角色菜单关联关系
        menuScopeService.deleteByRoleIdList(Collections.singletonList(id));
        // 删除角色用户关联关系
        roleUserService.deleteByRoleIdList(Collections.singletonList(id));
    }

}

package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.dao.RoleDao;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.entity.RoleUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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
    protected void beforeSaveOrUpdateDto(RoleDTO dto, int type) {
        if (0 == type) {
            // 新增，检查角色编码是否存在
            boolean hasRecord = hasIdRecord(dto.getId());
            AssertUtils.isTrue(hasRecord, "编码[" + dto.getId() + "]已存在");
        }
    }

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
    public List<String> getRoleIdListByUserId(@NotNull Long userId) {
        return CollStreamUtil.toList(roleUserService.lambdaQuery()
                .select(RoleUserEntity::getRoleId)
                .eq(RoleUserEntity::getUserId, userId)
                .groupBy(RoleUserEntity::getRoleId)
                .list(), RoleUserEntity::getRoleId);
    }

    /**
     * 根据角色id查询用户列表
     *
     * @param roleIds 角色id
     */
    public List<Long> getUserIdListByRoleIdList(List<String> roleIds) {
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
     * 删除数据本身及关联关系
     *
     * @param id 角色id
     */
    public void deleteAllById(@NotNull String id) {
        // 删除角色
        removeById(id);
        // 删除角色菜单关联关系
        menuScopeService.deleteByRoleIdList(Collections.singletonList(id));
        // 删除角色用户关联关系
        roleUserService.deleteByRoleIdList(Collections.singletonList(id));
    }

}

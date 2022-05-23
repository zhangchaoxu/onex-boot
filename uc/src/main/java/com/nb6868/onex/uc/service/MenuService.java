package com.nb6868.onex.uc.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.shiro.ShiroDao;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.MenuDao;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 菜单管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MenuService extends DtoService<MenuDao, MenuEntity, MenuDTO> {

    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private ShiroDao shiroDao;

    @Override
    protected void beforeSaveOrUpdateDto(MenuDTO dto, int type) {
        if (1 == type && dto.getId().equals(dto.getPid())) {
            // 更新 上级菜单不能为自身
            throw new OnexException(ErrorCode.ERROR_REQUEST, "上级不能为自身");
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, MenuDTO dto, MenuEntity existedEntity, int type) {
        if (ret && type == 1) {
            // 更新的时候,同时更新menu_scope中的信息
            menuScopeService.update().set("menu_permissions", dto.getPermissions()).eq("menu_id", dto.getId()).update(new MenuScopeEntity());
        }
    }

    /**
     * 获得用户菜单列表
     *
     * @param userType 用户类型
     * @param tenantCode 租户编码
     * @param userId 用户id
     */
    public List<MenuEntity> getListByUser(Integer userType, String tenantCode, Long userId, Integer menuType, Integer showMenu) {
        if (userType == UcConst.UserTypeEnum.SUPER_ADMIN.value() || userType == UcConst.UserTypeEnum.TENANT_ADMIN.value()) {
            // 系统/租户管理员
            // 获得对应租户下所有菜单内容
            return query()
                    .eq(menuType != null, "type", menuType)
                    .eq(showMenu != null, "show_menu", showMenu)
                    .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                    .isNull(StrUtil.isBlank(tenantCode), "tenant_code")
                    .orderByAsc("sort")
                    .list();
        } else {
            // 其它用户
            // 获得scope内菜单id,再获取范围角色对应
            List<Long> menuIds = shiroDao.getMenuIdListByUserId(userId);
            if (ObjectUtils.isEmpty(menuIds)) {
                return new ArrayList<>();
            } else {
                return query()
                        .eq(menuType != null, "type", menuType)
                        .eq(showMenu != null, "show_menu", showMenu)
                        .eq(StrUtil.isNotBlank(tenantCode), "tenant_code", tenantCode)
                        .isNull(StrUtil.isBlank(tenantCode), "tenant_code")
                        .in("id", menuIds)
                        .orderByAsc("sort")
                        .list();
            }
        }
    }

    /**
     * 递归上级菜单列表
     *
     * @param id 菜单ID
     */
    public List<MenuDTO> getParentList(Long id) {
        List<MenuDTO> menus = new ArrayList<>();
        while (id != 0) {
            MenuDTO dto = getDtoById(id);
            if (dto != null) {
                menus.add(dto);
                id = dto.getPid();
            } else {
                id = 0L;
            }
        }
        Collections.reverse(menus);
        return menus;
    }

    /**
     * 查询所有级联的子节点id
     */
    public List<Long> getCascadeChildrenListByIds(List<Long> ids) {
        List<Long> menuIds = new ArrayList<>();
        while (ids.size() > 0) {
            menuIds.addAll(ids);
            ids = listObjs(new QueryWrapper<MenuEntity>().select("id").in("pid", ids), o -> Long.valueOf(String.valueOf(o)));
        }
        return menuIds;
    }

    /**
     * 级联删除,删除菜单id以及下面所有子菜单
     *
     * @param id 菜单id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllCascadeById(Long id) {
        // 获得所有菜单id
        List<Long> menuIds = getCascadeChildrenListByIds(Collections.singletonList(id));
        // 删除所有菜单
        logicDeleteByIds(menuIds);
        // 删除菜单授权关系
        menuScopeService.deleteByMenuIds(menuIds);
    }

    /**
     * 保存角色和菜单的关系
     *
     * @param roleId 角色ID
     * @param menuIds  菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByRoleIdAndMenuIds(Long roleId, List<Long> menuIds) {
        // 先删除角色菜单关系
        menuScopeService.deleteByRoleIdList(Collections.singletonList(roleId));

        if (ObjectUtil.isNotEmpty(menuIds)) {
            listByIds(menuIds).forEach(menu -> {
                //保存角色菜单关系
                MenuScopeEntity menuScope = new MenuScopeEntity();
                menuScope.setType(UcConst.MenuScopeTypeEnum.ROLE.value());
                menuScope.setMenuId(menu.getId());
                menuScope.setMenuPermissions(menu.getPermissions());
                menuScope.setRoleId(roleId);
                menuScopeService.save(menuScope);
            });
        }
    }

    /**
     * 保存用户和菜单的关系
     *
     * @param userId  用户ID
     * @param menuIds 菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByUserIdAndMenuIds(Long userId, List<Long> menuIds) {
        // 先删除用户菜单关系
        menuScopeService.deleteByUserIdList(Collections.singletonList(userId));

        if (ObjectUtil.isNotEmpty(menuIds)) {
            //保存用户菜单关系
            listByIds(menuIds).forEach(menu -> {
                MenuScopeEntity menuScope = new MenuScopeEntity();
                menuScope.setType(UcConst.MenuScopeTypeEnum.USER.value());
                menuScope.setMenuId(menu.getId());
                menuScope.setMenuPermissions(menu.getPermissions());
                menuScope.setUserId(userId);
                menuScopeService.save(menuScope);
            });
        }
    }

}

package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dao.MenuDao;
import com.nb6868.onexboot.api.modules.uc.entity.MenuScopeEntity;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.TreeUtils;
import com.nb6868.onexboot.api.modules.uc.dto.MenuDTO;
import com.nb6868.onexboot.api.modules.uc.dto.MenuTreeDTO;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.service.MenuService;
import com.nb6868.onexboot.api.modules.uc.service.MenuScopeService;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * 菜单管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MenuServiceImpl extends CrudServiceImpl<MenuDao, MenuEntity, MenuDTO> implements MenuService {

    @Autowired
    private MenuScopeService menuScopeService;

    @Override
    public QueryWrapper<MenuEntity> getWrapper(String method, Map<String, Object> params) {
        return new QueryWrapper<>();
    }

    @Override
    protected void beforeSaveOrUpdateDto(MenuDTO dto, int type) {
        if (1 == type && dto.getId().equals(dto.getPid())) {
            // 更新 上级菜单不能为自身
            throw new OnexException(ErrorCode.SUPERIOR_MENU_ERROR);
        }
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, MenuDTO dto, MenuEntity existedEntity, int type) {
        if (ret && type == 1) {
            // 更新的时候,同时更新menu_scope中的信息
            menuScopeService.update().set("menu_permissions", dto.getPermissions()).eq("menu_id", dto.getId()).update(new MenuScopeEntity());
        }
    }

    @Override
    public List<MenuTreeDTO> getTreeByType(Integer type) {
        List<MenuEntity> menuList = getListByType(type);
        List<MenuTreeDTO> dtoList = ConvertUtils.sourceToTarget(menuList, MenuTreeDTO.class);
        return TreeUtils.build(dtoList);
    }

    @Override
    public List<MenuTreeDTO> getTreeByUser(UserDetail user, Integer type) {
        List<MenuEntity> entityList = getListByUser(user, type);
        List<MenuTreeDTO> dtoList = ConvertUtils.sourceToTarget(entityList, MenuTreeDTO.class);
        return TreeUtils.build(dtoList);
    }

    @Override
    public List<MenuEntity> getListByType(Integer type) {
        return query().eq(type != null, "type", type).orderByAsc("sort").list();
    }

    @Override
    public List<MenuEntity> getListByUser(UserDetail user, Integer type) {
        // 系统管理员，拥有最高权限
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            return getListByType(type);
        } else {
            return getBaseMapper().getListByUserId(user.getId(), type);
        }
    }

    @Override
    public List<String> getPermissionsList() {
        return listObjs(new QueryWrapper<MenuEntity>().select("permissions").ne("permissions", ""), Object::toString);
    }

    @Override
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

    @Override
    public List<Long> getCascadeChildrenListByIds(List<Long> ids) {
        List<Long> menuIds = new ArrayList<>();
        while (ids.size() > 0) {
            menuIds.addAll(ids);
            ids = listObjs(new QueryWrapper<MenuEntity>().select("id").in("pid", ids), o -> Long.valueOf(String.valueOf(o)));
        }
        return menuIds;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicDeleteByIds(Collection<? extends Serializable> idList) {
        List<Long> ids = (List<Long>) idList;
        // 删除角色菜单关系
        menuScopeService.deleteByMenuIds(ids);
        // 删除菜单
        return super.logicDeleteByIds(idList);
    }

}

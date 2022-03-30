package com.nb6868.onex.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.MenuDao;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
     * 用户Url列表
     *
     * @param user 用户
     */
    public List<MenuEntity> getListByUser(ShiroUser user, Integer type) {
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            // 系统管理员,返回所有内容
            return query().eq(type != null, "type", type).orderByAsc("sort").list();
        } else {
            // 普通用户,返回范围角色对应
            List<Long> menuIds = menuScopeService.getMenuIdListByUserId(user.getId());
            if (ObjectUtils.isEmpty(menuIds)) {
                return new ArrayList<>();
            } else {
                return query().eq(type != null, "type", type).in("id", menuIds).orderByAsc("sort").list();
            }
        }
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

}

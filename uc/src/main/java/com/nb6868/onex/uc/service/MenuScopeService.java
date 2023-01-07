package com.nb6868.onex.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.MenuScopeDao;
import com.nb6868.onex.uc.entity.MenuScopeEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色与菜单对应关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class MenuScopeService extends EntityService<MenuScopeDao, MenuScopeEntity> {

    /**
     * 根据角色编码，获取菜单ID列表
     *
     * @param roleId 角色ID
     */
    public List<Long> getMenuIdListByRoleId(@NotNull Long roleId) {
        return listObjs(new QueryWrapper<MenuScopeEntity>()
                .select("menu_id")
                .eq("role_id", roleId), o -> Long.valueOf(String.valueOf(o)));
    }

    /**
     * 根据用户id，删除用户菜单关系
     *
     * @param userIds 用户ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserIdList(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(update().in("user_id", userIds).eq("type", UcConst.MenuScopeTypeEnum.USER.value()));
        }
    }

    /**
     * 根据角色id，删除角色菜单关系
     *
     * @param roleIds 角色ID数组
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleIdList(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(update().in("role_id", roleIds).eq("type", UcConst.MenuScopeTypeEnum.ROLE.value()));
        }
    }

    /**
     * 根据菜单id，删除角色菜单关系
     *
     * @param menuIds 菜单ids
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return true;
        } else {
            return logicDeleteByWrapper(update().in("menu_id", menuIds));
        }
    }

}

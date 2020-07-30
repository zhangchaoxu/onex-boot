package com.nb6868.onex.modules.uc.service.impl;

import com.nb6868.onex.booster.service.impl.BaseServiceImpl;
import com.nb6868.onex.modules.uc.dao.RoleMenuDao;
import com.nb6868.onex.modules.uc.entity.RoleMenuEntity;
import com.nb6868.onex.modules.uc.service.RoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 角色与菜单对应关系
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class RoleMenuServiceImpl extends BaseServiceImpl<RoleMenuDao, RoleMenuEntity> implements RoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long roleId, String roleCode, List<Long> menuIds) {
        // 先删除角色菜单关系
        deleteByRoleIds(Collections.singletonList(roleId));

        if (ObjectUtils.isNotEmpty(menuIds)) {
            //保存角色菜单关系
            for (Long menuId : menuIds) {
                RoleMenuEntity roleMenu = new RoleMenuEntity();
                roleMenu.setMenuId(menuId);
                roleMenu.setRoleCode(roleCode);
                roleMenu.setRoleId(roleId);
                //保存
                save(roleMenu);
            }
        }
    }

    @Override
    public List<Long> getMenuIdListByRoleId(Long roleId) {
        return listObjs(new QueryWrapper<RoleMenuEntity>().select("menu_id").eq("role_id", roleId),  o -> Long.valueOf(String.valueOf(o)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleIds(List<Long> roleIds) {
        return logicDeleteByWrapper(new QueryWrapper<RoleMenuEntity>().in("role_id", roleIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        return logicDeleteByWrapper(new QueryWrapper<RoleMenuEntity>().in("menu_id", menuIds));
    }

}

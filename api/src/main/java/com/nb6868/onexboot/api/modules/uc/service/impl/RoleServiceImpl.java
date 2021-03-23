package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.uc.dao.RoleDao;
import com.nb6868.onexboot.api.modules.uc.dto.RoleDTO;
import com.nb6868.onexboot.api.modules.uc.entity.RoleEntity;
import com.nb6868.onexboot.api.modules.uc.entity.RoleUserEntity;
import com.nb6868.onexboot.api.modules.uc.service.MenuScopeService;
import com.nb6868.onexboot.api.modules.uc.service.RoleService;
import com.nb6868.onexboot.api.modules.uc.service.RoleUserService;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class RoleServiceImpl extends CrudServiceImpl<RoleDao, RoleEntity, RoleDTO> implements RoleService {

    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private RoleUserService roleUserService;

    @Override
    public QueryWrapper<RoleEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<RoleEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .getQueryWrapper();
    }

    @Override
    public List<Long> getRoleIdList() {
        return listObjs(new QueryWrapper<RoleEntity>().select("id"), o -> Long.valueOf(String.valueOf(o)));
    }

    @Override
    public List<Long> getRoleIdListByUserId(Long userId) {
        return roleUserService.listObjs(new QueryWrapper<RoleUserEntity>().select("role_id").eq("user_id", userId), o -> Long.valueOf(String.valueOf(o)));
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, RoleDTO dto, RoleEntity existedEntity, int type) {
        if (ret) {
            // 保存角色菜单关系
            menuScopeService.saveOrUpdateByRoleAndMenuIds(dto.getId(), dto.getName(), dto.getMenuIdList());
            // 如果是更新,则更新角色用户表中的角色名字段
            if (1 == type) {
                roleUserService.update().set("role_name", dto.getName()).eq("role_id", dto.getId()).update(new RoleUserEntity());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicDeleteByIds(Collection<? extends Serializable> idList) {
        List<Long> ids = (List<Long>) idList;
        // 删除角色用户关系
        roleUserService.deleteByRoleIds(ids);
        // 删除角色菜单关系
        menuScopeService.deleteByRoleIds(ids);
        return super.logicDeleteByIds(idList);
    }

}

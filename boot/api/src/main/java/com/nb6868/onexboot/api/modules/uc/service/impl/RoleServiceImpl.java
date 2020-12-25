package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dao.RoleDao;
import com.nb6868.onexboot.api.modules.uc.dto.RoleDTO;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.api.modules.uc.entity.RoleEntity;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
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
    private RoleMenuService roleMenuService;
    @Autowired
    private RoleDataScopeService roleDataScopeService;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private DeptService deptService;

    @Override
    public QueryWrapper<RoleEntity> getWrapper(String method, Map<String, Object> params) {
        QueryWrapper<RoleEntity> qw = new WrapperUtils<RoleEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .like("code", "code")
                .getQueryWrapper();

        // 普通管理员，只能查询所属部门及子部门的数据
        UserDetail user = SecurityUser.getUser();
        if (user.getType() > UcConst.UserTypeEnum.SYSADMIN.value()) {
            List<Long> deptIdList = deptService.getSubDeptIdList(user.getDeptId());
            qw.in(deptIdList != null, "dept_id", deptIdList);
        }

        return qw;
    }

    @Override
    public List<String> getRoleCodeList() {
        return listObjs(new QueryWrapper<RoleEntity>().select("code"), Object::toString);
    }

    @Override
    public List<String> getRoleCodeListByUserId(Long userId) {
        return getBaseMapper().getRoleCodeListByUserId(userId);
    }

    @Override
    protected void beforeSaveOrUpdateDto(RoleDTO dto, int type) {
        AssertUtils.isTrue(hasDuplicated(dto.getId(), "code", dto.getCode()), ErrorCode.ERROR_REQUEST, "编码不存在");
    }

    @Override
    protected void afterSaveOrUpdateDto(boolean ret, RoleDTO dto, RoleEntity existedEntity, int type) {
        // 保存角色菜单关系
        roleMenuService.saveOrUpdate(dto.getId(), dto.getCode(), dto.getMenuIdList());
        // 保存角色数据权限关系
        roleDataScopeService.saveOrUpdate(dto.getId(), dto.getDeptIdList());
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logicDeleteByIds(Collection<? extends Serializable> idList) {
        List<Long> ids = (List<Long>) idList;
        // 删除角色用户关系
        roleUserService.deleteByRoleIds(ids);
        // 删除角色菜单关系
        roleMenuService.deleteByRoleIds(ids);
        // 删除角色数据权限关系
        roleDataScopeService.deleteByRoleIds(ids);
        return super.logicDeleteByIds(idList);
    }

}

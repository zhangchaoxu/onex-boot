package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.dto.*;
import com.nb6868.onex.uc.entity.DeptEntity;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.service.MenuScopeService;
import com.nb6868.onex.uc.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UcRole")
@RequestMapping("/uc/role/")
@Validated
@Tag(name = "角色管理")
public class RoleController {

    @Autowired
    RoleService roleService;
    @Autowired
    MenuScopeService menuScopeService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<PageData<RoleDTO>> page(@Validated({PageGroup.class}) @RequestBody RoleQueryReq form) {
        PageData<RoleDTO> page = roleService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<PageData<RoleDTO>>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<List<RoleDTO>> list(@Validated @RequestBody RoleQueryReq form) {
        List<RoleDTO> list = roleService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<List<RoleDTO>>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<RoleDTO> info(@Validated @RequestBody IdReq req) {
        RoleDTO data = roleService.oneDto(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 查询角色对应的菜单
        List<Long> menuIdList = menuScopeService.getMenuIdListByRoleId(data.getId());
        data.setMenuIdList(menuIdList);
        return new Result<RoleDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:edit"}, logical = Logical.OR)
    public Result<?> saveOrUpdate(@Validated @RequestBody RoleSaveOrUpdateReq req) {
        RoleEntity entity = roleService.saveOrUpdateByReq(req);
        RoleDTO dto = ConvertUtils.sourceToTarget(entity, RoleDTO.class);

        return new Result<>().success(dto);
    }

    @PostMapping("updateState")
    @Operation(summary = "更新状态")
    @LogOperation("更新状态")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:edit"}, logical = Logical.OR)
    public Result<?> updateState(@Validated @RequestBody RoleUpdateStateReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(roleService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 状态变更
        boolean ret = roleService.lambdaUpdate().eq(RoleEntity::getId, req.getId()).set(RoleEntity::getState, req.getState()).update(new RoleEntity());
        // 返回结果
        return new Result<>().bool(ret);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:delete"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        AssertUtils.isFalse(roleService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        roleService.deleteAllById(req.getId());
        return new Result<>();
    }

}

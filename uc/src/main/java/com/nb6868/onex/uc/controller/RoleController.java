package com.nb6868.onex.uc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.pojo.StringIdForm;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.dto.RoleQueryForm;
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

@RestController
@RequestMapping("/uc/role")
@Validated
@Tag(name = "角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuScopeService menuScopeService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody RoleQueryForm form) {
        PageData<?> page = roleService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 20)
    public Result<?> list(@Validated @RequestBody RoleQueryForm form) {
        List<?> list = roleService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 30)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody StringIdForm form) {
        RoleDTO data = roleService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        // 查询角色对应的菜单
        List<Long> menuIdList = menuScopeService.getMenuIdListByRoleId(data.getId());
        data.setMenuIdList(menuIdList);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody RoleDTO dto) {
        roleService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody RoleDTO dto) {
        roleService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:role:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 60)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdForm form) {
        // 判断数据是否存在
        AssertUtils.isFalse(roleService.hasIdRecord(form.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除
        roleService.deleteAllById(form.getId());
        return new Result<>();
    }

}

package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.RoleDTO;
import com.nb6868.onex.uc.dto.RoleQueryForm;
import com.nb6868.onex.uc.entity.RoleEntity;
import com.nb6868.onex.uc.service.MenuScopeService;
import com.nb6868.onex.uc.service.RoleService;
import com.nb6868.onex.uc.service.RoleUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/uc/role")
@Validated
@Api(tags = "角色管理", position = 30)
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private RoleUserService roleUserService;

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:role:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody RoleQueryForm form) {
        QueryWrapper<RoleEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<RoleDTO> page = roleService.pageDto(form.getPage(), queryWrapper);
        return new Result<>().success(page);
    }

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:role:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> list(@Validated @RequestBody RoleQueryForm form) {
        QueryWrapper<RoleEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        List<RoleDTO> list = roleService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:role:query")
    public Result<?> info(@Validated @RequestBody IdForm form) {
        RoleDTO data = roleService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        // 查询角色对应的菜单
        List<Long> menuIdList = menuScopeService.getMenuIdListByRoleId(data.getId());
        data.setMenuIdList(menuIdList);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:role:edit")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody RoleDTO dto) {
        roleService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:role:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody RoleDTO dto) {
        roleService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:role:delete")
    public Result<?> delete(@RequestBody IdForm form) {
        // 判断数据是否存在
        RoleDTO data = roleService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除角色
        roleService.logicDeleteById(data.getId());
        // 删除角色菜单关联关系
        menuScopeService.deleteByRoleIdList(Collections.singletonList(data.getId()));
        // 删除角色用户关联关系
        roleUserService.deleteByRoleIds(Collections.singletonList(data.getId()));
        return new Result<>();
    }

}

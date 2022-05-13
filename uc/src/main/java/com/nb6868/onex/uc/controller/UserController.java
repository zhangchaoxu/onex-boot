package com.nb6868.onex.uc.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.dto.UserQueryForm;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.DeptService;
import com.nb6868.onex.uc.service.RoleService;
import com.nb6868.onex.uc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/user")
@Validated
@Api(tags = "用户管理", position = 20)
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    DeptService deptService;

    @PostMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:user:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody UserQueryForm form) {
        PageData<?> page = userService.pageDto(form.getPage(), QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:user:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> list(@Validated({PageGroup.class}) @RequestBody UserQueryForm form) {
        List<?> list = userService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:user:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody IdTenantForm form) {
        UserDTO data = userService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 用户角色列表
        data.setRoleIds(roleService.getRoleIdListByUserId(form.getId()));
        // 部门树
        data.setDeptChain(deptService.getParentChain(data.getDeptCode()));
        return new Result<>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:user:edit")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserDTO dto) {
        userService.saveDto(dto);

        return new Result<>();
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:user:edit")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserDTO dto) {
        userService.updateDto(dto);

        return new Result<>();
    }

    @PostMapping("changeState")
    @ApiOperation("更新状态")
    @LogOperation("更新状态")
    @RequiresPermissions("uc:user:edit")
    public Result<?> changeState(@Validated(value = {DefaultGroup.class, ChangeStateForm.BoolStateGroup.class}) @RequestBody ChangeStateForm request) {
        userService.changeState(request);

        return new Result<>();
    }

    @PostMapping("changeMenuScope")
    @ApiOperation("修改用户授权")
    @LogOperation("修改用户授权")
    @RequiresPermissions("uc:user:changeMenuScope")
    public Result<?> changeMenuScope(@RequestBody List<Long> menuIds) {
        userService.changeMenuScope(menuIds);

        return new Result<>();
    }

    @PostMapping("delete")
    @LogOperation("删除")
    @ApiOperation("删除")
    @RequiresPermissions("uc:user:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdTenantForm form) {
        // 判断数据是否存在
        UserEntity data = userService.getOne(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isTrue(Objects.equals(ShiroUtils.getUserId(), data.getId()), "无法删除当前登录用户");
        // 删除
        userService.deleteAllByIds(Collections.singletonList(data.getId()));

        return new Result<>();
    }

}

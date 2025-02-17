package com.nb6868.onex.uc.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.ChangeStateReq;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.dto.UserQueryReq;
import com.nb6868.onex.uc.dto.UserSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.DeptService;
import com.nb6868.onex.uc.service.RoleService;
import com.nb6868.onex.uc.service.UserService;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController("UcUser")
@RequestMapping("/uc/user/")
@Validated
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    DeptService deptService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<PageData<UserDTO>> page(@Validated({PageGroup.class}) @RequestBody UserQueryReq form) {
        QueryWrapper<UserEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        if (CollUtil.isNotEmpty(form.getRoleCodes())) {
            List<Long> userIds = roleService.getUserIdListByRoleCodeList(form.getRoleCodes());
            if (CollUtil.isEmpty(userIds)) {
                return new Result<PageData<UserDTO>>().success(new PageData<>());
            }
            queryWrapper.in("id", userIds);
        } else if (CollUtil.isNotEmpty(form.getRoleIds())) {
            List<Long> userIds = roleService.getUserIdListByRoleIdList(form.getRoleIds());
            if (CollUtil.isEmpty(userIds)) {
                return new Result<PageData<UserDTO>>().success(new PageData<>());
            }
            queryWrapper.in("id", userIds);
        }
        PageData<UserDTO> page = userService.pageDto(form, queryWrapper);

        return new Result<PageData<UserDTO>>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<List<UserDTO>> list(@Validated @RequestBody UserQueryReq form) {
        QueryWrapper<UserEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "list");
        if (CollUtil.isNotEmpty(form.getRoleCodes())) {
            List<Long> userIds = roleService.getUserIdListByRoleCodeList(form.getRoleCodes());
            if (CollUtil.isEmpty(userIds)) {
                return new Result<List<UserDTO>>().success(CollUtil.newArrayList());
            }
            queryWrapper.in("id", userIds);
        } else if (CollUtil.isNotEmpty(form.getRoleIds())) {
            List<Long> userIds = roleService.getUserIdListByRoleIdList(form.getRoleIds());
            if (CollUtil.isEmpty(userIds)) {
                return new Result<List<UserDTO>>().success(CollUtil.newArrayList());
            }
            queryWrapper.in("id", userIds);
        }
        List<UserDTO> list = userService.listDto(queryWrapper);

        return new Result<List<UserDTO>>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<UserDTO> info(@Validated @RequestBody IdReq form) {
        UserDTO data = userService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 用户角色列表
        data.setRoleIds(roleService.getRoleIdListByUserId(form.getId()));
        // 部门树
        data.setDeptChain(deptService.getParentChain(data.getDeptCode()));
        return new Result<UserDTO>().success(data);
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增或更新")
    @LogOperation("新增或更新")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<UserDTO> saveOrUpdate(@RequestBody UserSaveOrUpdateReq req) {
        UserEntity entity = userService.saveOrUpdateByReq(req);
        UserDTO dto = ConvertUtils.sourceToTarget(entity, UserDTO.class);

        return new Result<UserDTO>().success(dto);
    }

    @PostMapping("changeState")
    @Operation(summary = "更新状态")
    @LogOperation("更新状态")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<?> changeState(@Validated(value = {DefaultGroup.class, ChangeStateReq.BoolStateGroup.class}) @RequestBody ChangeStateReq request) {
        userService.changeState(request);

        return new Result<>();
    }

    @PostMapping("changeMenuScope")
    @Operation(summary = "修改用户授权")
    @LogOperation("修改用户授权")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<?> changeMenuScope(@RequestBody List<Long> menuIds) {
        userService.changeMenuScope(menuIds);

        return new Result<>();
    }

    @PostMapping("delete")
    @LogOperation("删除")
    @Operation(summary = "删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:delete"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq form) {
        // 判断数据是否存在
        UserEntity data = userService.getOne(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isTrue(Objects.equals(ShiroUtils.getUserId(), data.getId()), "无法删除当前登录用户");
        // 删除
        userService.deleteAllByIds(Collections.singletonList(data.getId()));
        return new Result<>();
    }

}

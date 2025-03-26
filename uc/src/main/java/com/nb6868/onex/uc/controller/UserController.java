package com.nb6868.onex.uc.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.*;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
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
import java.util.Map;
import java.util.Objects;

@RestController("UcUser")
@RequestMapping("/uc/user/")
@Validated
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    AuthProps authProps;
    @Autowired
    ParamsService paramsService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    DeptService deptService;
    @Autowired
    AuthService authService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<PageData<UserDTO>> page(@Validated({PageGroup.class}) @RequestBody UserQueryReq req) {
        QueryWrapper<UserEntity> queryWrapper = userService.buildQueryWrapper(req, "page");
        PageData<UserDTO> page = userService.pageDto(req, queryWrapper);
        page.getList().forEach(userDTO -> {
            // 需要部门
            userDTO.setDeptList(req.isDeptNeeded() ? CollStreamUtil.toList(deptService.getDeptListByUserId(userDTO.getId(), UcConst.DeptUserTypeEnum.DEFAULT.getCode()), entity -> BeanUtil.copyProperties(entity, DeptRes.class)) : CollUtil.newArrayList());
            // 需要角色
            userDTO.setRoleList(req.isRoleNeeded() ? CollStreamUtil.toList(roleService.getRoleListByUserId(userDTO.getId()), entity -> BeanUtil.copyProperties(entity, RoleRes.class)) : CollUtil.newArrayList());
        });
        return new Result<PageData<UserDTO>>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<List<UserDTO>> list(@Validated @RequestBody UserQueryReq req) {
        QueryWrapper<UserEntity> queryWrapper = userService.buildQueryWrapper(req, "page");
        List<UserDTO> list = userService.listDto(queryWrapper);
        list.forEach(userDTO -> {
            // 需要部门
            userDTO.setDeptList(req.isDeptNeeded() ? CollStreamUtil.toList(deptService.getDeptListByUserId(userDTO.getId(), UcConst.DeptUserTypeEnum.DEFAULT.getCode()), entity -> BeanUtil.copyProperties(entity, DeptRes.class)) : CollUtil.newArrayList());
            // 需要角色
            userDTO.setRoleList(req.isRoleNeeded() ? CollStreamUtil.toList(roleService.getRoleListByUserId(userDTO.getId()), entity -> BeanUtil.copyProperties(entity, RoleRes.class)) : CollUtil.newArrayList());
        });
        return new Result<List<UserDTO>>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:query"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<UserDTO> info(@Validated @RequestBody IdReq req) {
        UserDTO data = userService.oneDto(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 需要部门
        data.setDeptList(CollStreamUtil.toList(deptService.getDeptListByUserId(data.getId(), UcConst.DeptUserTypeEnum.DEFAULT.getCode()), entity -> BeanUtil.copyProperties(entity, DeptRes.class)));
        // 需要角色
        data.setRoleList(CollStreamUtil.toList(roleService.getRoleListByUserId(data.getId()), entity -> BeanUtil.copyProperties(entity, RoleRes.class)));
        return new Result<UserDTO>().success(data);
    }

    @PostMapping("updateRole")
    @Operation(summary = "更新用户角色关系")
    @LogOperation("更新用户角色关系")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<?> updateRole(@RequestBody UserUpdateRoleReq req) {
        userService.updateRole(req);
        return new Result<>().success();
    }

    @PostMapping("updateDept")
    @Operation(summary = "更新用户部门关系")
    @LogOperation("更新用户部门关系")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<?> updateDept(@RequestBody UserUpdateDeptReq req) {
        userService.updateDept(req);
        return new Result<>().success();
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

    @PostMapping("updatePassword")
    @Operation(summary = "更新密码")
    @LogOperation("更新密码")
    public Result<?> updatePassword(@Validated @RequestBody UserUpdatePasswordReq req) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(req.getType());
        AssertUtils.isNull(loginParams, "缺少[" + req.getType() + "]登录配置");
        // 先对密码做解密
        String newPasswordPlaintext = PasswordUtils.aesDecode(req.getNewPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        AssertUtils.isEmpty(newPasswordPlaintext, "密码传输请做加密处理");
        // 对新密码密码强度做校验
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(loginParams.getStr("passwordRegExp")) && !ReUtil.isMatch(loginParams.getStr("passwordRegExp"), newPasswordPlaintext), ErrorCode.ERROR_REQUEST, loginParams.getStr("passwordRegError", "密码不符合规则"));
        // 获取数据库中的用户
        AssertUtils.isFalse(userService.hasIdRecord(req.getId()), ErrorCode.DB_RECORD_NOT_EXISTED);
        // 更新密码
        userService.updatePassword(req.getId(), newPasswordPlaintext, authProps.getPasswordStoreKey());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIdList(Collections.singletonList(req.getId()));
        return new Result<>();
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
    public Result<?> changeMenuScope(@Validated @RequestBody UserUpdateMenuScopeReq req) {
        userService.changeMenuScope(req.getId(), req.getMenuIds());
        return new Result<>();
    }

    @PostMapping("delete")
    @LogOperation("删除")
    @Operation(summary = "删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:delete"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        UserEntity data = userService.getOne(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isTrue(Objects.equals(ShiroUtils.getUserId(), data.getId()), "无法删除当前登录用户");
        // 删除
        userService.deleteAllByIds(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("listLockUser")
    @Operation(summary = "获得所有的锁定用户及时间")
    @LogOperation("获得所有的锁定用户及时间")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<?> listLockUser(@Validated @RequestBody BaseReq req) {
        Map<String, String> map = authService.getAllUserLockTime();
        return new Result<>().success(map);
    }

    @PostMapping("removeLockUser")
    @Operation(summary = "解除锁定用户")
    @LogOperation("解除锁定用户")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:user:edit"}, logical = Logical.OR)
    public Result<?> removeLockUser(@Validated @RequestBody IdReq req) {
        // 判断数据是否存在
        UserEntity data = userService.getOne(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        authService.removeUserLockTime(data.getUsername());
        return new Result<>();
    }

}

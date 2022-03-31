package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.ChangePasswordForm;
import com.nb6868.onex.uc.dto.UserDTO;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/user")
@Validated
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    AuthProps authProps;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    DeptService deptService;

    @DataSqlScope(tableAlias = "uc_user", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:user:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<UserDTO> list = userService.listDto(params);

        return new Result<>().success(list);
    }

    @DataSqlScope(tableAlias = "uc_user", tenantFilter = true)
    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:user:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<UserDTO> page = userService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:user:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        UserDTO data = userService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 用户角色列表
        data.setRoleCodes(roleService.getRoleCodeListByUserId(id));
        // 部门树
        data.setDeptChain(deptService.getParentChain(data.getDeptId()));
        return new Result<>().success(data);
    }

    @GetMapping("userInfo")
    @ApiOperation("登录用户信息")
    public Result<?> userInfo() {
        UserDTO data = ConvertUtils.sourceToTarget(ShiroUtils.getUser(), UserDTO.class);
        return new Result<>().success(data);
    }

    @PostMapping("changePassword")
    @ApiOperation("修改密码")
    @LogOperation("修改密码")
    public Result<?> changePassword(@Validated @RequestBody ChangePasswordForm dto) {
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验原密码
        AssertUtils.isFalse(PasswordUtils.verify(dto.getPassword(), data.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 更新密码
        userService.updatePassword(data.getId(), dto.getNewPassword());
        return new Result<>();
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:user:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserDTO dto) {
        userService.saveDto(dto);

        return new Result<>();
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:user:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserDTO dto) {
        userService.updateDto(dto);

        return new Result<>();
    }

    @PostMapping("changeState")
    @ApiOperation("更新状态")
    @LogOperation("更新状态")
    @RequiresPermissions("uc:user:update")
    public Result<?> changeState(@Validated(value = {DefaultGroup.class, ChangeStateRequest.BoolStateGroup.class}) @RequestBody ChangeStateRequest request) {
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

    @PostMapping("deleteBatch")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:user:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        userService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @PostMapping("logout")
    @ApiOperation(value = "退出")
    @LogOperation(value = "退出", type = "logout")
    public Result<?> logout() {
        String token = HttpContextUtils.getRequestParameter(authProps.getTokenKey());
        userService.logout(token);
        return new Result<>();
    }

}

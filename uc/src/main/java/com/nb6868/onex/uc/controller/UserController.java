package com.nb6868.onex.uc.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.portal.modules.uc.UcConst;
import com.nb6868.onex.portal.modules.uc.dto.ChangePasswordByMailCodeRequest;
import com.nb6868.onex.portal.modules.uc.dto.PasswordDTO;
import com.nb6868.onex.portal.modules.uc.dto.UserDTO;
import com.nb6868.onex.portal.modules.uc.entity.UserEntity;
import com.nb6868.onex.portal.modules.uc.excel.UserExcel;
import com.nb6868.onex.portal.modules.uc.service.DeptService;
import com.nb6868.onex.portal.modules.uc.service.RoleService;
import com.nb6868.onex.portal.modules.uc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
        data.setRoleIdList(roleService.getRoleIdListByUserId(id));
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

    @PutMapping("password")
    @ApiOperation("修改密码")
    @LogOperation("修改密码")
    public Result<?> password(@Validated @RequestBody PasswordDTO dto) {
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验原密码
        AssertUtils.isFalse(PasswordUtils.verify(dto.getPassword(), data.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);

        userService.updatePassword(data.getId(), dto.getNewPassword());
        return new Result<>();
    }

    /**
     * 通过验证码修改密码
     * 忘记密码功能,通过验证码找回
     */
    @PostMapping("changePasswordByMailCode")
    @ApiOperation(value = "通过短信验证码修改密码")
    @AccessControl("/changePasswordByMailCode")
    public Result<?> changePasswordBySmsCode(@Validated @RequestBody ChangePasswordByMailCodeRequest request) {
        boolean ret = userService.changePasswordBySmsCode(request);
        return new Result<>().success();
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:user:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserDTO dto) {
        userService.saveDto(dto);

        return new Result<>();
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:user:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserDTO dto) {
        userService.updateDto(dto);

        return new Result<>();
    }

    @PutMapping("changeState")
    @ApiOperation("更新状态")
    @LogOperation("更新状态")
    @RequiresPermissions("uc:user:update")
    public Result<?> changeState(@Validated(value = {DefaultGroup.class, ChangeStateRequest.BoolStateGroup.class}) @RequestBody ChangeStateRequest request) {
        userService.changeState(request);

        return new Result<>();
    }

    @GetMapping("getMenuScope")
    @ApiOperation("获得用户授权")
    @RequiresPermissions("uc:user:changeMenuScope")
    public Result<?> getMenuScope() {
        // todo 返回用户角色授权和用户自身授权
        // userService.changeMenuScope(menuIds);

        return new Result<>();
    }

    @PutMapping("changeMenuScope")
    @ApiOperation("修改用户授权")
    @LogOperation("修改用户授权")
    @RequiresPermissions("uc:user:changeMenuScope")
    public Result<?> changeMenuScope(@RequestBody List<Long> menuIds) {
        userService.changeMenuScope(menuIds);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:user:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        userService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @PostMapping("logout")
    @ApiOperation(value = "退出")
    @LogOperation(value = "退出", type = "logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = HttpContextUtils.getRequestParameter(request, UcConst.TOKEN_HEADER);
        userService.logout(token);
        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("uc:user:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<UserDTO> list = userService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "用户", list, UserExcel.class);
    }

    @PostMapping("import")
    @ApiOperation("导入")
    @LogOperation("导入")
    @RequiresPermissions("uc:user:import")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file, @RequestParam Long deptId, @RequestParam List<Long> roleIds) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);

        ImportParams params = new ImportParams();
        params.setStartSheetIndex(0);
        List<UserExcel> list = ExcelUtils.importExcel(file, UserExcel.class, params);
        AssertUtils.isTrue(list.isEmpty(), ErrorCode.ERROR_REQUEST, "Excel内容为空");
        AssertUtils.isTrue(list.size() > Const.EXCEL_IMPORT_LIMIT, ErrorCode.ERROR_REQUEST, "单次导入不得超过" + Const.EXCEL_IMPORT_LIMIT + "条");

        List<MsgResult> result = new ArrayList<>();
        for (UserExcel item : list) {
            UserDTO dto = ConvertUtils.sourceToTarget(item, UserDTO.class);
            MsgResult validateResult = ValidatorUtils.getValidateResult(dto, DefaultGroup.class, AddGroup.class);
            if (validateResult.isSuccess()) {
                // 额外赋值
                dto.setDeptId(deptId);
                dto.setRoleIdList(roleIds);
                dto.setState(1);
                try {
                    userService.saveDto(dto);
                    result.add(new MsgResult().success("导入成功"));
                } catch (Exception e) {
                    result.add(new MsgResult().error(ErrorCode.ERROR_REQUEST, e.getMessage()));
                }
            } else {
                result.add(validateResult);
            }
        }
        return new Result<>().success(result);
    }

}

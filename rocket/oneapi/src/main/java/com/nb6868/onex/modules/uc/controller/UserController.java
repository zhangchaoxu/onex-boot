package com.nb6868.onex.modules.uc.controller;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.pojo.MsgResult;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.util.JacksonUtils;
import com.nb6868.onex.booster.util.bcrypt.BCryptPasswordEncoder;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.booster.validator.ValidatorUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.DataFilter;
import com.nb6868.onex.common.annotation.LogLogin;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.AESUtils;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.dto.*;
import com.nb6868.onex.modules.uc.entity.RoleUserEntity;
import com.nb6868.onex.modules.uc.excel.UserExcel;
import com.nb6868.onex.modules.uc.service.DeptService;
import com.nb6868.onex.modules.uc.service.RoleUserService;
import com.nb6868.onex.modules.uc.service.UserService;
import com.nb6868.onex.modules.uc.user.SecurityUser;
import com.nb6868.onex.modules.uc.user.UserDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Charles
 */
@RestController
@RequestMapping("uc/user")
@Validated
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private DeptService deptService;

    @DataFilter(tableAlias = "uc_user", tenantFilter = true)
    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:user:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<UserDTO> list = userService.listDto(params);

        return new Result<>().success(list);
    }

    @DataFilter(tableAlias = "uc_user", tenantFilter = true)
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
        data.setRoleIdList(roleUserService.getRoleIdList(id));
        // 部门树
        data.setDeptChain(deptService.getParentChain(data.getDeptId()));
        return new Result<UserDTO>().success(data);
    }

    @GetMapping("userInfo")
    @ApiOperation("登录用户信息")
    public Result<?> userInfo() {
        UserDTO data = ConvertUtils.sourceToTarget(SecurityUser.getUser(), UserDTO.class);
        return new Result<UserDTO>().success(data);
    }

    @PutMapping("password")
    @ApiOperation("修改密码")
    @LogOperation("修改密码")
    public Result<?> password(@Validated @RequestBody PasswordDTO dto) {
        UserDetail user = SecurityUser.getUser();
        UserDTO userDTO = userService.getDtoById(user.getId());
        AssertUtils.isNull(userDTO, ErrorCode.DB_RECORD_NOT_EXISTED);

        // 原密码不正确
        if (!new BCryptPasswordEncoder().matches(dto.getPassword(), user.getPassword())) {
            return new Result<>().error(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }

        userService.updatePassword(user.getId(), dto.getNewPassword());

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

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:user:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserDTO dto) {
        userService.updateDto(dto);

        return new Result<>();
    }

    @PutMapping("changeStatus")
    @ApiOperation("更新状态")
    @LogOperation("更新状态")
    @RequiresPermissions("uc:user:update")
    public Result<?> changeStatus(@RequestBody UserDTO dto) {
        userService.changeStatus(dto);

        return new Result<>();
    }

    @DeleteMapping("delete")
    @LogOperation("删除")
    @RequiresPermissions("uc:user:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        userService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:user:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        userService.logicDeleteByIds(ids);

        return new Result<>();
    }

    /**
     * 加密登录
     * 逻辑同login接口
     */
    @SneakyThrows
    @PostMapping("loginEncrypt")
    @ApiOperation(value = "加密登录")
    @AnonAccess
    @LogLogin
    public Result<?> loginEncrypt(HttpServletRequest request, @RequestBody String loginEncrypted) {
        // 密文转json明文
        String loginRaw = AESUtils.decrypt(URLDecoder.decode(loginEncrypted, "UTF-8"));
        // json明文转实体
        LoginRequest loginRequest = JacksonUtils.jsonToPojo(loginRaw, LoginRequest.class);

        // 效验数据
        ValidatorUtils.validateEntity(loginRequest, DefaultGroup.class);

        return new Result<>().success(userService.login(loginRequest));
    }

    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation(value = "登录")
    @AnonAccess
    @LogLogin
    public Result<?> login(@Validated(value = {DefaultGroup.class}) @RequestBody LoginRequest loginRequest) {
        return new Result<>().success(userService.login(loginRequest));
    }

    /**
     * 注册
     */
    @PostMapping("register")
    @ApiOperation(value = "注册")
    @AnonAccess
    public Result<?> register(@Validated @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * 通过短信验证码修改密码
     * 忘记密码功能,通过短信验证码找回
     */
    @PostMapping("changePasswordBySmsCode")
    @ApiOperation(value = "通过短信验证码修改密码")
    @AnonAccess
    public Result<?> changePasswordBySmsCode(@Validated @RequestBody ChangePasswordBySmsCodeRequest request) {
        return userService.changePasswordBySmsCode(request);
    }

    @PostMapping("logout")
    @ApiOperation(value = "退出")
    @LogLogin(type = UcConst.LoginTypeEnum.LOGOUT)
    public Result<?> logout() {
        userService.logout();

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
    public Result<?> importExcel(@RequestParam("file") MultipartFile file, @RequestParam(value = "deptId") Long deptId) {
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
                dto.setStatus(1);
                try {
                    userService.saveDto(dto);
                    // 插入用户与角色关系表
                    RoleUserEntity roleUser = new RoleUserEntity();
                    roleUser.setUserId(dto.getId());
                    roleUserService.save(roleUser);
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

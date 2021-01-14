package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.api.common.annotation.AccessControl;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.common.util.ExcelUtils;
import com.nb6868.onexboot.api.modules.sys.dto.ParamDTO;
import com.nb6868.onexboot.api.modules.sys.excel.ParamExcel;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dto.LoginConfigAdmin;
import com.nb6868.onexboot.api.modules.uc.dto.LoginTypeConfig;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 参数管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("sys/param")
@Validated
@Api(tags = "参数管理")
public class ParamController {

    @Autowired
    private ParamService paramService;

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("sys:param:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ParamDTO> page = paramService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("sys:param:info")
    public Result<?> info(@NotNull(message = "{id.require}") @RequestParam Long id) {
        ParamDTO data = paramService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @GetMapping("getContentByCode")
    @ApiOperation("通过code获取对应参数的content")
    @AccessControl
    public Result<?> getContentByCode(@NotBlank(message = "code不能为空") @RequestParam String code) {
        String content = paramService.getContent(code);

        return new Result<>().success(JacksonUtils.jsonToMap(content));
    }

    @GetMapping("getContentByCodes")
    @ApiOperation("通过code获取对应参数的content")
    @AccessControl
    public Result<?> getContentByCodes(@NotBlank(message = "codes不能为空") @RequestParam String codes) {
        List<String> codeList = StringUtils.splitToList(codes);
        Kv kv = Kv.init();
        for (String code : codeList) {
            String content = paramService.getContent(code);
            if (UcConst.LOGIN_CONFIG_ADMIN.equalsIgnoreCase(code)) {
                // 用户登录信息
                LoginConfigAdmin loginConfigAdmin = JacksonUtils.jsonToPojo(content, LoginConfigAdmin.class);
                if (null != loginConfigAdmin) {
                    if (loginConfigAdmin.isUsernamePasswordLogin()) {
                        loginConfigAdmin.setUsernamePasswordLoginConfig(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name(), LoginTypeConfig.class));
                    }
                    if (loginConfigAdmin.isMobileSmscodeLogin()) {
                        loginConfigAdmin.setMobileSmscodeLoginConfig(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name(), LoginTypeConfig.class));
                    }
                }
                kv.set(code, loginConfigAdmin);
            } else {
                kv.set(code, JacksonUtils.jsonToMap(content));
            }
        }
        return new Result<>().success(kv);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:param:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ParamDTO dto) {
        paramService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:param:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ParamDTO dto) {
        paramService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:param:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        paramService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("sys:param:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        paramService.logicDeleteByIds(ids);

        return new Result<>();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:param:export")
    @ApiImplicitParam(name = "code", value = "参数编码", paramType = "query", dataType = "String")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ParamDTO> list = paramService.listDto(params);

        ExcelUtils.exportExcelToTarget(response, "参数", list, ParamExcel.class);
    }

    @GetMapping("clearCache")
    @ApiOperation("清空缓存")
    @RequiresPermissions("sys:param:clearCache")
    public Result<?> clearCache(@RequestParam(required = false) String key) {
        paramService.clearCache(key);
        return new Result<>().success();
    }

}

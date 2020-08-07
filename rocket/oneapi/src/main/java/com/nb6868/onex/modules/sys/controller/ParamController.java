package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.booster.pojo.Kv;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.JacksonUtils;
import com.nb6868.onex.booster.util.StringUtils;
import com.nb6868.onex.booster.validator.group.AddGroup;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import com.nb6868.onex.booster.validator.group.UpdateGroup;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.util.ExcelUtils;
import com.nb6868.onex.modules.sys.dto.ParamDTO;
import com.nb6868.onex.modules.sys.excel.ParamExcel;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.dto.LoginAdminCfg;
import com.nb6868.onex.modules.uc.dto.LoginChannelCfg;
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

        return new Result<>().success(data);
    }

    @GetMapping("getContentByCode")
    @ApiOperation("通过code获取对应参数的content")
    @AnonAccess
    public Result<?> getContentByCode(@NotBlank(message = "code不能为空") @RequestParam String code) {
        String content = paramService.getContent(code);

        return new Result<>().success(JacksonUtils.jsonToMap(content));
    }

    @GetMapping("getContentByCodes")
    @ApiOperation("通过code获取对应参数的content")
    @AnonAccess
    public Result<?> getContentByCodes(@NotBlank(message = "codes不能为空") @RequestParam String codes) {
        List<String> codeList = StringUtils.splitToList(codes);
        Kv kv = Kv.init();
        for (String code : codeList) {
            String content = paramService.getContent(code);
            if (UcConst.LOGIN_ADMIN_CFG.equalsIgnoreCase(code)) {
                // 用户登录信息
                LoginAdminCfg loginAdminCfg = JacksonUtils.jsonToPojo(content, LoginAdminCfg.class);
                if (null != loginAdminCfg) {
                    if (loginAdminCfg.isLoginByUsernameAndPassword()) {
                        LoginChannelCfg channelCfg = paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + UcConst.LoginTypeEnum.ADMIN_USER_PWD.value(), LoginChannelCfg.class);
                        loginAdminCfg.setLoginByUsernameAndPasswordCfg(channelCfg);
                    }
                    if (loginAdminCfg.isLoginByMobileAndSmsCode()) {
                        LoginChannelCfg channelCfg = paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + UcConst.LoginTypeEnum.ADMIN_MOBILE_SMS.value(), LoginChannelCfg.class);
                        loginAdminCfg.setLoginByMobileAndSmsCodeCfg(channelCfg);
                    }
                    /*if (loginAdminCfg.isLoginByWechatScan()) {
                        LoginChannelCfg channelCfg = paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + UcConst.LoginTypeEnum.ADMIN_WECHAT.value(), LoginChannelCfg.class);
                        loginAdminCfg.setLoginByWechatScanCfg(channelCfg);
                    }
                    if (loginAdminCfg.isLoginByDingtalkScan()) {
                        LoginChannelCfg channelCfg = paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.value(), LoginChannelCfg.class);
                        loginAdminCfg.setLoginByDingtalkScanCfg(channelCfg);
                    }*/
                }
                kv.set(code, loginAdminCfg);
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
    @AnonAccess
    public Result<?> clearCache(@RequestParam(required = false) String key) {
        paramService.clearCache(key);
        return new Result<>().success();
    }

}

package com.nb6868.onexboot.api.modules.uc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.common.pojo.Result;

/**
 * 认证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("uc/auth")
@Validated
@Api(tags = "用户认证")
public class AuthController {

    @Autowired
    private OnexProps onexProps;
    @Autowired
    private ParamService paramService;

    @GetMapping("getLoginAdmin")
    @ApiOperation("获得后台登录配置")
    public Result<?> getLoginAdmin() {

        return new Result<>().success(onexProps);
        /*Map<String, Object> loginAdminConfig = paramService.getContentMap(UcConst.LOGIN_ADMIN);
        AssertUtils.isNull(loginAdminConfig, "未找到后台登录配置");

        if ((boolean)loginAdminConfig.get("usernamePasswordLogin")) {
            loginAdminConfig.put("usernamePasswordLoginConfig", paramService.getContentMap(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name()));
        }
        if ((boolean)loginAdminConfig.get("mobileSmscodeLogin")) {
            loginAdminConfig.put("mobileSmscodeLoginConfig", paramService.getContentMap(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name()));
        }
        if ((boolean)loginAdminConfig.get("dingtalkScanLogin")) {
            loginAdminConfig.put("dingtalkScanLoginConfig", paramService.getContentMap(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name()));
        }
        if ((boolean)loginAdminConfig.get("wechatScanLogin")) {
            loginAdminConfig.put("wechatScanLoginConfig", paramService.getContentMap(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name()));
        }
        return new Result<>().success(loginAdminConfig);*/
    }

}

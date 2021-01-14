package com.nb6868.onexboot.api.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 后台登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "后台登录配置")
public class LoginConfigAdmin implements Serializable {

    @ApiModelProperty(value = "开放注册")
    private boolean register;

    @ApiModelProperty(value = "开放忘记密码")
    private boolean forgetPassword;

    @ApiModelProperty(value = "开放帐号密码登录")
    private boolean usernamePasswordLogin;

    @ApiModelProperty(value = "帐号密码登录配置")
    private LoginTypeConfig usernamePasswordLoginConfig;

    @ApiModelProperty(value = "开放手机验证码登录")
    private boolean mobileSmscodeLogin;

    @ApiModelProperty(value = "手机验证码登录配置")
    private LoginTypeConfig mobileSmscodeLoginConfig;

    @ApiModelProperty(value = "开放微信扫码登录")
    private boolean wechatScanLogin;

    @ApiModelProperty(value = "微信扫码登录配置")
    private LoginTypeConfig wechatScanLoginConfig;

    @ApiModelProperty(value = "开放钉钉扫码登录")
    private boolean dingtalkScanLogin;

    @ApiModelProperty(value = "钉钉扫码登录配置")
    private LoginTypeConfig dingtalkScanLoginConfig;

}

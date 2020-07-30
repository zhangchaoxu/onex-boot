package com.nb6868.onex.modules.uc.dto;

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
public class LoginAdminCfg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开放注册")
    private boolean register;

    @ApiModelProperty(value = "开放忘记密码")
    private boolean forgetPassword;

    @ApiModelProperty(value = "开放帐号密码登录")
    private boolean loginByUsernameAndPassword;

    @ApiModelProperty(value = "帐号密码登录配置")
    private LoginChannelCfg loginByUsernameAndPasswordCfg;

    @ApiModelProperty(value = "开放手机验证码登录")
    private boolean loginByMobileAndSmsCode;

    @ApiModelProperty(value = "手机验证码登录配置")
    private LoginChannelCfg loginByMobileAndSmsCodeCfg;

    @ApiModelProperty(value = "开放微信扫码登录")
    private boolean loginByWechatScan;

    @ApiModelProperty(value = "开放钉钉扫码登录")
    private boolean loginByDingtalkScan;

    @ApiModelProperty(value = "微信扫码登录配置")
    private LoginChannelCfg loginByWechatScanCfg;

    @ApiModelProperty(value = "钉钉扫码登录配置")
    private LoginChannelCfg loginByDingtalkScanCfg;

}

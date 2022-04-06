package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录请求")
public class LoginForm implements Serializable {
    /**
     * 验证码校验
     */
    public interface CaptchaGroup { }

    /**
     * 帐号密码登录校验
     */
    public interface UsernamePasswordGroup { }

    /**
     * 短信验证码登录校验
     */
    public interface MobileSmsCodeGroup { }

    /**
     * 租户校验
     */
    public interface TenantGroup { }

    @ApiModelProperty(value = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    private String authConfigType;

    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "用户名不能为空", groups = {UsernamePasswordGroup.class})
    private String username;

    @ApiModelProperty(value = "密码")
    @NotEmpty(message = "密码不能为空", groups = {UsernamePasswordGroup.class})
    private String password;

    @ApiModelProperty(value = "手机号区域")
    private String mobileArea = "86";

    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "手机号不能为空", groups = {MobileSmsCodeGroup.class})
    private String mobile;

    @ApiModelProperty(value = "短信验证码")
    @NotEmpty(message = "短信验证码不能为空", groups = {MobileSmsCodeGroup.class})
    private String smsCode;

    @ApiModelProperty(value = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "验证码不能为空", groups = {CaptchaGroup.class})
    private String captchaValue;

    @ApiModelProperty(value = "验证码标识")
    @NotEmpty(message = "验证码标识不能为空", groups = {CaptchaGroup.class})
    private String captchaUuid;

}

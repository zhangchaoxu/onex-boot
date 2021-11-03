package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 登录请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录请求")
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 苹果登录校验
     */
    public interface AppleGroup { }

    @ApiModelProperty(value = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    private String authConfigKey;

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

    @ApiModelProperty(value = "验证码")
    @NotEmpty(message = "验证码不能为空", groups = {CaptchaGroup.class})
    private String captcha;

    @ApiModelProperty(value = "唯一标识")
    @NotEmpty(message = "唯一标识不能为空", groups = {CaptchaGroup.class})
    private String uuid;

    @ApiModelProperty(value = "苹果登录token(jwt)")
    @NotEmpty(message = "苹果登录token不能为空", groups = {AppleGroup.class})
    private String appleIdentityToken;

}

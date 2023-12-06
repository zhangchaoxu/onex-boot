package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "登录请求")
public class LoginForm implements Serializable {
    /**
     * 验证码校验
     */
    public interface CaptchaGroup {
    }

    /**
     * 帐号密码登录校验
     */
    public interface UsernamePasswordGroup {
    }

    /**
     * 短信验证码登录校验
     */
    public interface MobileSmsGroup {
    }

    @Schema(description = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    @NotEmpty(message = "请指定登录类型", groups = {DefaultGroup.class})
    private String type;

    @Schema(description = "用户名")
    @NotEmpty(message = "用户名不能为空", groups = {UsernamePasswordGroup.class})
    private String username;

    @Schema(description = "密码")
    @NotEmpty(message = "密码不能为空", groups = {UsernamePasswordGroup.class})
    private String password;

    @Schema(description = "手机号区域")
    private String mobileArea = "86";

    @Schema(description = "手机号")
    @NotEmpty(message = "手机号不能为空", groups = {MobileSmsGroup.class})
    private String mobile;

    @Schema(description = "短信验证码")
    @NotEmpty(message = "短信验证码不能为空", groups = {MobileSmsGroup.class})
    private String sms;

    @Schema(description = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @Schema(description = "验证码")
    @NotEmpty(message = "验证码不能为空", groups = {CaptchaGroup.class})
    private String captchaValue;

    @Schema(description = "验证码标识")
    @NotEmpty(message = "验证码标识不能为空", groups = {CaptchaGroup.class})
    private String captchaUuid;

}

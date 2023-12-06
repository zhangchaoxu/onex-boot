package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户名密码登录请求")
public class UsernamePasswordLoginForm extends BaseForm {

    @Schema(description = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    private String type = "ADMIN_USERNAME_PASSWORD";

    @Schema(description = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @Schema(description = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空", groups = {LoginForm.UsernamePasswordGroup.class})
    private String username;

    @Schema(description = "密码", required = true)
    @NotEmpty(message = "密码不能为空", groups = {LoginForm.UsernamePasswordGroup.class})
    private String password;

    @Schema(description = "验证码值")
    @NotEmpty(message = "验证码值不能为空", groups = {LoginForm.CaptchaGroup.class})
    private String captchaValue;

    @Schema(description = "验证码标识")
    @NotEmpty(message = "验证码标识不能为空", groups = {LoginForm.CaptchaGroup.class})
    private String captchaUuid;

}

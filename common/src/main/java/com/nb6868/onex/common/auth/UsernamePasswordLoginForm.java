package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户名密码登录请求")
public class UsernamePasswordLoginForm extends BaseForm {

    @ApiModelProperty(value = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    private String type = "ADMIN_USERNAME_PASSWORD";

    @ApiModelProperty(value = "租户编码", required = true)
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @ApiModelProperty(value = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空", groups = {LoginForm.UsernamePasswordGroup.class})
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码不能为空", groups = {LoginForm.UsernamePasswordGroup.class})
    private String password;

    @ApiModelProperty(value = "验证码值")
    @NotEmpty(message = "验证码值不能为空", groups = {LoginForm.CaptchaGroup.class})
    private String captchaValue;

    @ApiModelProperty(value = "验证码标识")
    @NotEmpty(message = "验证码标识不能为空", groups = {LoginForm.CaptchaGroup.class})
    private String captchaUuid;

}

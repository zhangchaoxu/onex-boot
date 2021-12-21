package com.nb6868.onex.common.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户名密码登录请求")
public class UsernamePasswordLoginForm implements Serializable {

    @ApiModelProperty(value = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    private String authConfigType = "ADMIN_USERNAME_PASSWORD";

    @ApiModelProperty(value = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空", groups = {LoginForm.UsernamePasswordGroup.class})
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码不能为空", groups = {LoginForm.UsernamePasswordGroup.class})
    private String password;

}

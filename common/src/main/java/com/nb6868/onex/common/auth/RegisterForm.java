package com.nb6868.onex.common.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 注册请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "注册请求")
public class RegisterForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "密码", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "短信验证码", required = true)
    @NotEmpty(message = "短信验证码不能为空")
    private String smsCode;

    @Schema(description = "手机号区域")
    private String mobileArea = "86";

    @Schema(description = "手机号", required = true)
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String username;

}

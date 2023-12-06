package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通过验证码修改密码请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "通过验证码修改密码请求")
public class ChangePasswordByMailCodeForm extends BaseForm {

    @Schema(description = "新密码", required = true)
    @NotEmpty(message = "新密码不能为空")
    private String password;

    @Schema(description = "验证码", required = true)
    @NotEmpty(message = "验证码不能为空")
    private String smsCode;

    @Schema(description = "收件方", required = true)
    @NotEmpty(message = "收件方不能为空")
    private String mailTo;

}

package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "修改密码请求")
public class ChangePasswordForm extends BaseForm {

     @Schema(description = "原密码", required = true)
    @NotEmpty(message = "原密码不能为空")
    private String password;

     @Schema(description = "新密码", required = true)
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

}

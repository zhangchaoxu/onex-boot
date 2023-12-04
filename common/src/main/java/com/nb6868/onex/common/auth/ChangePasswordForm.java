package com.nb6868.onex.common.auth;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "修改密码请求")
public class ChangePasswordForm extends BaseForm {

    @ApiModelProperty(value = "原密码", required = true)
    @NotEmpty(message = "原密码不能为空")
    private String password;

    @ApiModelProperty(value = "新密码", required = true)
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

}

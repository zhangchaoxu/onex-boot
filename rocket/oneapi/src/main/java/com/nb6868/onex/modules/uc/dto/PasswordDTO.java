package com.nb6868.onex.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 修改密码
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "修改密码")
public class PasswordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "原密码")
    @NotBlank(message = "{sysuser.password.require}")
    private String password;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "{sysuser.password.require}")
    private String newPassword;

}

package com.nb6868.onexboot.api.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 通过验证码修改密码请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "通过验证码修改密码请求")
public class ChangePasswordByMailCodeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "验证码", required = true)
    @NotEmpty(message = "验证码不能为空")
    private String smsCode;

    @ApiModelProperty(value = "收件方", required = true)
    @NotEmpty(message = "收件方不能为空")
    private String mailTo;

}

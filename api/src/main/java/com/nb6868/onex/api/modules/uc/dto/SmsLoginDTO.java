package com.nb6868.onex.api.modules.uc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 短信登录表单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "短信登录表单")
public class SmsLoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "短信验证码")
    @NotBlank(message = "短信验证码不能为空")
    private String code;

    @ApiModelProperty(value = "登录类型")
    @NotBlank(message = "{type.require}")
    private String type;

    @ApiModelProperty(value = "验证码")
    private String captcha;

    @ApiModelProperty(value = "唯一标识")
    private String uuid;

}

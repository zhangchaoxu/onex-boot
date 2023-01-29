package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "验证码请求")
public class CaptchaForm extends BaseForm {

    @ApiModelProperty(value = "验证码图片宽度", example = "150")
    @Range(min = 10, max = 4000, message = "宽度范围10-4000")
    @NotNull(message = "宽度不能为空")
    private Integer width;

    @ApiModelProperty(value = "验证码图片高度", example = "50")
    @Range(min = 10, max = 4000, message = "高度范围10-4000")
    @NotNull(message = "高度不能为空")
    private Integer height;

}

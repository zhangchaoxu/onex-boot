package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "验证码请求")
public class CaptchaForm extends BaseForm {

    @ApiModelProperty(value = "宽度", example = "150")
    @Range(min = 10, max = 4000, message = "宽度范围10-4000")
    private Integer width = 150;

    @ApiModelProperty(value = "高度", example = "50")
    @Range(min = 10, max = 4000, message = "高度范围10-4000")
    private Integer height = 50;

}

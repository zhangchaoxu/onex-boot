package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "验证码请求")
public class CaptchaForm extends BaseForm {

    @Schema(description = "验证码图片宽度,范围10-4000", example = "150", required = true)
    @Range(min = 10, max = 4000, message = "宽度范围10-4000")
    @NotNull(message = "宽度不能为空")
    private Integer width;

    @Schema(description = "验证码图片高度,范围10-4000", example = "50", required = true)
    @Range(min = 10, max = 4000, message = "高度范围10-4000")
    @NotNull(message = "高度不能为空")
    private Integer height;

}

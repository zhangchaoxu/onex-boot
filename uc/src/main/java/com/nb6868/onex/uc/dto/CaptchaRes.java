package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "验证码结果")
public class CaptchaRes extends BaseRes {

    @Schema(description = "验证码唯一id,后续请求需带入")
    private String uuid;

    @Schema(description = "验证码base64格式")
    private String image;

}

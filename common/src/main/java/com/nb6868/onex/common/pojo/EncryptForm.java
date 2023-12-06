package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "加密请求体")
public class EncryptForm extends BaseForm {

     @Schema(description = "加密请求密文", required = true)
    @NotEmpty(message = "请求内容不能为空")
    private String body;

}

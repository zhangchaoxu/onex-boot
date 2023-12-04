package com.nb6868.onex.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "加密请求体")
public class EncryptForm extends BaseForm {

    @ApiModelProperty(value = "加密请求密文", required = true)
    @NotEmpty(message = "请求内容不能为空")
    private String body;

}

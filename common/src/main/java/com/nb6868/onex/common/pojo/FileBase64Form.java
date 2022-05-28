package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "文件表单")
public class FileBase64Form extends BaseForm {

    @ApiModelProperty(value = "base64文件内容", required = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String fileBase64;

    @ApiModelProperty(value = "文件名")
    private String filaName;

}

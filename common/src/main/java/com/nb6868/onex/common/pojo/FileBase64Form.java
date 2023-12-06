package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "文件表单")
public class FileBase64Form extends BaseForm {

     @Schema(description = "base64文件内容", required = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String fileBase64;

     @Schema(description = "文件名")
    private String filaName;

}

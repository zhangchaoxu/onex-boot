package com.nb6868.onex.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "文件base64上传")
public class OssFileBase64UploadForm extends BaseForm {

    @Schema(description = "配置参数名")
    private String paramsCode = "OSS_PUBLIC";

    @Schema(description = "文件前缀")
    private String prefix;

    @Schema(description = "base64文件内容")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message = "文件base64不能为空")
    private String fileBase64;

    @Schema(description = "文件名")
    @NotEmpty(message = "文件名不能为空")
    private String filaName;

}

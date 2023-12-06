package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.FileBase64Form;
import io.swagger.v3.oas.annotations.media.Schema;
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

     @Schema(description = "文件上传表单")
    private FileBase64Form fileBase64;

}

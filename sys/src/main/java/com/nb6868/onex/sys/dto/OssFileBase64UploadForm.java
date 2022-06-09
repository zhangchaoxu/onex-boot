package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.pojo.FileBase64Form;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "文件base64上传")
public class OssFileBase64UploadForm extends BaseForm {

    @ApiModelProperty(value = "配置参数名")
    private String paramsCode = "OSS_PUBLIC";

    @ApiModelProperty(value = "文件前缀")
    private String prefix;

    @ApiModelProperty(value = "文件上传表单")
    private FileBase64Form fileBase64;

}

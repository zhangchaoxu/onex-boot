package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "获得授权访问地址请求")
public class OssPresignedUrlForm extends BaseForm {

     @Schema(description = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

     @Schema(description = "文件key")
    @NotEmpty(message = "objectName不能为空")
    private String objectName;

     @Schema(description = "过期描述")
    private Long expiration = 36000L;

}

package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseForm;
import com.nb6868.onex.common.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "获得授权访问地址请求")
public class OssPreSignedUrlForm extends BaseForm {

    @Schema(description = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

    @Schema(description = "文件key")
    @NotEmpty(message = "objectKey不能为空")
    private String objectKey;

    @Schema(description = "请求method")
    @NotEmpty(message = "method不能为空")
    @EnumValue(strValues = {"put", "get"}, message = "method只支持put和get")
    private String method;

    @Schema(description = "过期秒数")
    private int expire = 3600;

}

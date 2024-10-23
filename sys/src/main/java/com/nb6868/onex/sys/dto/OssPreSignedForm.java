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
public class OssPreSignedForm extends BaseForm {

    @Schema(description = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

    @Schema(description = "路径前缀")
    private String prefix;

    @Schema(description = "文件key")
    @NotEmpty(message = "文件名不能为空")
    private String fileName;

    @Schema(description = "请求method")
    @NotEmpty(message = "method不能为空")
    @EnumValue(strValues = {"put", "get", "post"}, message = "method只支持put get post")
    private String method = "post";

    @Schema(description = "过期秒数")
    private int expire = 3600;

}

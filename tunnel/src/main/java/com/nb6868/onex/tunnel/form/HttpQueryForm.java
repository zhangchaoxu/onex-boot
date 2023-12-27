package com.nb6868.onex.tunnel.form;

import com.nb6868.onex.common.pojo.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Http查询请求")
public class HttpQueryForm extends BaseForm {

    @Schema(description = "method")
    @NotBlank(message = "method不能为空")
    private String method;

    @Schema(description = "url")
    @NotBlank(message = "url不能为空")
    private String url;

    @Schema(description = "body")
    private String body;

    @Schema(description = "超时毫秒")
    private int timeout;

    @Schema(description = "请求参数")
    Map<String, Object> params;

    @Schema(description = "header参数")
    Map<String, String> headers;

}

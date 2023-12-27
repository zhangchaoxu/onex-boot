package com.nb6868.onex.tunnel.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "系统参数查询")
public class SystemQuery {

    @Schema(description = "属性名")
    @NotBlank(message = "属性名不能为空")
    private String name;

    @Schema(description = "安静模式，不将出错信息打在System.err")
    private boolean quiet;

}

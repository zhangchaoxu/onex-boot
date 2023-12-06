package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务立即执行,指定参数")
public class JobRunWithParamsForm implements Serializable {

     @Schema(description = "id")
    @NotNull(message = "{id.require}")
    private Long id;

     @Schema(description = "参数")
    private JSONObject params;

}

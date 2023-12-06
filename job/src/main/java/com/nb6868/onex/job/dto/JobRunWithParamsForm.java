package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务立即执行,指定参数")
public class JobRunWithParamsForm implements Serializable {

    @ApiModelProperty(value = "id")
    @NotNull(message = "{id.require}")
    private Long id;

    @ApiModelProperty(value = "参数")
    private JSONObject params;

}

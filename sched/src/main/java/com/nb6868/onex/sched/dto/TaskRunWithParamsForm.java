package com.nb6868.onex.sched.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务立即执行,指定参数")
public class TaskRunWithParamsForm implements Serializable {

    @ApiModelProperty(value = "id")
    @NotNull(message = "{id.require}")
    private Long id;

    @ApiModelProperty(value = "参数")
    private String params;

}

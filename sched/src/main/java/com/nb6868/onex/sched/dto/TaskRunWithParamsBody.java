package com.nb6868.onex.sched.dto;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务立即执行,指定参数")
public class TaskRunWithParamsBody {

    @ApiModelProperty(value = "id")
    @NotNull(message = "{id.require}", groups = DefaultGroup.class)
    private Long id;

    @ApiModelProperty(value = "参数")
    private String params;

}

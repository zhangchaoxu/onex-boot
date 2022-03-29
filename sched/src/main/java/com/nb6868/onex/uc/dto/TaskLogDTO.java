package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务执行日志")
public class TaskLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "参数")
    private String params;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "结果")
    private String result;

    @ApiModelProperty(value = "错误信息")
    private String error;

    @ApiModelProperty(value = "耗时(单位：毫秒)")
    private Long times;

}

package com.nb6868.onex.sched.utils;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("定时任务执行结果")
public class ScheduleRunResult implements Serializable {

    @ApiModelProperty("将结果保存到数据库")
    private Boolean logToDb = false;

    @ApiModelProperty("结果")
    private Dict result;

}

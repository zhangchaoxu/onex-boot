package com.nb6868.onex.uc.utils;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel("定时任务执行结果")
public class ScheduleRunResult implements Serializable {

    @ApiModelProperty("将结果保存到数据库")
    private Boolean logToDb;

    @ApiModelProperty("结果")
    private Dict result;

    public ScheduleRunResult(Dict result) {
        this.result = result;
    }
}

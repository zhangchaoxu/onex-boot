package com.nb6868.onex.job.sched;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel("定时任务执行结果")
public class JobRunResult implements Serializable {

    @ApiModelProperty("将结果保存到数据库")
    private Boolean logToDb;

    @ApiModelProperty("结果")
    private JSONObject result;

    public JobRunResult(JSONObject result) {
        this.result = result;
    }
}

package com.nb6868.onex.job.sched;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel("定时任务执行结果")
public class JobRunResult implements Serializable {

    @Schema(description = "将结果保存到数据库")
    private Boolean logToDb = false;

    @Schema(description = "结果")
    private JSONObject result;

    public JobRunResult(JSONObject result) {
        this.result = result;
    }
}

package com.nb6868.onex.job.sched;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "定时任务执行结果")
public class JobRunRes extends BaseRes {

    @Schema(description = "将结果保存到数据库")
    private Boolean logToDb = false;

    @Schema(description = "结果")
    private JSONObject result;

    public JobRunRes(JSONObject result) {
        this.result = result;
    }
}

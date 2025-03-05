package com.nb6868.onex.job.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务明明细查询")
public class JobItemQueryReq extends PageReq {

    @Query
    @Schema(description = "任务ID")
    private Long jobId;

    @Query
    @Schema(description = "任务日志ID")
    private Long jobLogId;

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

}

package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.job.JobConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务日志")
public class JobLogDTO extends BaseDTO {

    @Schema(description = "创建者名字")
    private String createName;

    @Schema(description = "任务ID")
    private Long jobId;

    @Schema(description = "任务名称")
    private String jobCode;

    @Schema(description = "参数")
    private JSONObject params;

    @Schema(description = "日志状态")
    private Integer state;

    @Schema(description = "日志状态名称")
    public String getStateName() {
        return JobConst.JobLogStateEnum.getTitleByCode(state);
    }

    @Schema(description = "结果")
    private String result;

    @Schema(description = "错误信息")
    private String error;

    @Schema(description = "耗时(毫秒)")
    private Integer timeInterval;

    @Schema(description = "租户编码")
    private String tenantCode;

}

package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "系统-定时任务明细")
public class JobItemDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

	@Schema(description = "任务ID")
	private Long jobId;

	@Schema(description = "状态")
	private Integer state;

	@Schema(description = "结果")
	private String result;

	@Schema(description = "耗时")
	private Integer timeInterval;

	@Schema(description = "租户编码")
	private String tenantCode;

	@Schema(description = "任务日志ID")
	private Long jobLogId;

	@Schema(description = "原始数据")
	private JSONObject meta;

}

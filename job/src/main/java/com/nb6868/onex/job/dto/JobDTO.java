package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务")
public class JobDTO extends BaseDTO {

	 @Schema(description = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String code;

	 @Schema(description = "状态")
	@EnumValue(intValues = {0, 1}, message = "状态值错误", groups = DefaultGroup.class)
	private Integer state;

	 @Schema(description = "cron表达式")
	@NotBlank(message = "cron表达式不能为空", groups = DefaultGroup.class)
	private String cron;

	 @Schema(description = "日志类型")
	@NotBlank(message = "日志类型不能为空", groups = DefaultGroup.class)
	private String logType = "db";

	 @Schema(description = "参数")
	private JSONObject params;

	 @Schema(description = "备注")
	private String remark;

	 @Schema(description = "租户编码")
	private String tenantCode;

}

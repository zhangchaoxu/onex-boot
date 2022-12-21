package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务")
public class JobDTO extends BaseDTO {

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "状态")
	@EnumValue(intValues = {0, 1}, message = "状态值错误", groups = DefaultGroup.class)
	private Integer state;

	@ApiModelProperty(value = "cron表达式")
	@NotBlank(message = "cron表达式不能为空", groups = DefaultGroup.class)
	private String cron;

	@ApiModelProperty(value = "日志类型")
	@NotBlank(message = "日志类型不能为空", groups = DefaultGroup.class)
	private String logType = "db";

	@ApiModelProperty(value = "参数")
	private JSONObject params;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

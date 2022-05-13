package com.nb6868.onex.sys.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "定时任务日志")
public class SchedLogDTO extends BaseDTO {

	@ApiModelProperty(value = "任务ID")
	private Long taskId;

	@ApiModelProperty(value = "任务名称")
	private String taskName;

	@ApiModelProperty(value = "参数")
	private JSONObject params;

	@ApiModelProperty(value = "日志状态")
	private Integer state;

	@ApiModelProperty(value = "结果")
	private String result;

	@ApiModelProperty(value = "错误信息")
	private String error;

	@ApiModelProperty(value = "耗时(单位：毫秒)")
	private Integer times;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

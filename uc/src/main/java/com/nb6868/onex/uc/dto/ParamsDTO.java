package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户参数")
public class ParamsDTO extends BaseDTO {

	@ApiModelProperty(value = "用户ID")
	private Long userId;

	@ApiModelProperty(value = "数据开放范围")
	private String scope;

	@ApiModelProperty(value = "类型")
	@NotNull(message = "类型不能为空", groups = DefaultGroup.class)
	private Integer type;

	@ApiModelProperty(value = "编码")
	@NotEmpty(message = "编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "内容")
	private JSONObject content;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

	@ApiModelProperty(value = "备注")
	private String remark;

}

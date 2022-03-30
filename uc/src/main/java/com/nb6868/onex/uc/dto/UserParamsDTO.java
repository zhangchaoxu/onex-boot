package com.nb6868.onex.uc.dto;

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
public class UserParamsDTO extends BaseDTO {

	@ApiModelProperty(value = "编码")
	@NotEmpty(message = "编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "用户ID")
	@NotNull(message = "用户ID不能为空", groups = DefaultGroup.class)
	private Long userId;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

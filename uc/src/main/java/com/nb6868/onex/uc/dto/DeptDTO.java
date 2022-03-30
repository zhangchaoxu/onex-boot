package com.nb6868.onex.uc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 部门
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "部门")
public class DeptDTO extends BaseDTO {

	@ApiModelProperty(value = "上级ID")
	@NotNull(message="{pid.require}", groups = DefaultGroup.class)
	private Long pid;

	@ApiModelProperty(value = "上级IDs")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String pids;

	@ApiModelProperty(value = "部门名称")
	@NotBlank(message="{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "排序")
	@Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
	private Integer sort;

	@ApiModelProperty(value = "上级部门名称")
	private String parentName;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

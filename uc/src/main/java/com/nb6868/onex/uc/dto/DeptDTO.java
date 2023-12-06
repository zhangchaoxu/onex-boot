package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 部门
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "部门")
public class DeptDTO extends BaseDTO {

	@ApiModelProperty(value = "类型")
	@NotNull(message="类型不能为空", groups = DefaultGroup.class)
	private Integer type;

	@ApiModelProperty(value = "编码")
	@NotNull(message="编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "上级编码")
	@NotNull(message="上级编码不能为空", groups = DefaultGroup.class)
	private String pcode;

	@ApiModelProperty(value = "区域编码")
	private String areaCode;

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

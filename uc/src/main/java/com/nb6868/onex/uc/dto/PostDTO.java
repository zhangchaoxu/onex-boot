package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "岗位")
public class PostDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "编码")
	@NotBlank(message = "{code.require}", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "排序")
	@NotNull(message = "排序不能为空")
	private Integer sort;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

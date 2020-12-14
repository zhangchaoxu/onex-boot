package com.nb6868.onexboot.api.modules.uc.dto;

import com.nb6868.onexboot.common.util.TreeNode;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 部门(树状)
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "部门(树状)")
public class DeptTreeDTO extends TreeNode<DeptTreeDTO> {

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

}

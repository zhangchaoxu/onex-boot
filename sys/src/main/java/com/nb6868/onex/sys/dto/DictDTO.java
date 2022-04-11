package com.nb6868.onex.sys.dto;

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
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "数据字典")
public class DictDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "上级ID，一级为0")
	@NotNull(message="{pid.require}", groups = DefaultGroup.class)
	private Long pid;

	@ApiModelProperty(value = "字典类型")
	@NotBlank(message="{sysdict.type.require}", groups = DefaultGroup.class)
	private String type;

	@ApiModelProperty(value = "字典名称")
	@NotBlank(message="{sysdict.name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "字典值")
	private String value;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "排序")
	@Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
	private Integer sort;

}

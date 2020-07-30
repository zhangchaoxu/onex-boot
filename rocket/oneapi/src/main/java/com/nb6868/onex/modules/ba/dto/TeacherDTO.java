package com.nb6868.onex.modules.ba.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * 秉奥-教师
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "秉奥-教师")
public class TeacherDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	@NotEmpty(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "类型")
	private Integer type;

	@ApiModelProperty(value = "头像")
	@NotEmpty(message = "{avatar.require}", groups = DefaultGroup.class)
	private String imgs;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "状态0 未激活 1 激活")
	private Integer status;

}

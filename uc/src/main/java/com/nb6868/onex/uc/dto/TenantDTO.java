package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "租户")
public class TenantDTO extends BaseDTO {

	@ApiModelProperty(value = "编码")
	@NotBlank(message = "编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "名称不能为空", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "状态 0 无效 1 有效")
	@EnumValue(message = "状态指定值0和1", groups = DefaultGroup.class)
	private Integer state;

}

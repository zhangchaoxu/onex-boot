package com.nb6868.onexboot.api.modules.sys.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * 短地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "短地址")
public class ShorturlDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "标题")
	private String name;

	@ApiModelProperty(value = "完整地址")
	@NotBlank(message = "地址不能为空", groups = DefaultGroup.class)
	private String url;

	@ApiModelProperty(value = "短地址路径")
	private String code;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态 0 不开放 1 开放")
	@Range(min = 0, max = 1, message = "状态取值0-2", groups = DefaultGroup.class)
	private Integer state;

}

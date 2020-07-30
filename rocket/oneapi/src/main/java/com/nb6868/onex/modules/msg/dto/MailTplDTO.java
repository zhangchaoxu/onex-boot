package com.nb6868.onex.modules.msg.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 消息模板
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息模板")
public class MailTplDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "名称不能为空", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "编码")
	@NotBlank(message = "编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "类型 短信sms 电邮email 微信模板消息wx_template 站内信 notice")
	@NotBlank(message = "类型不能为空", groups = DefaultGroup.class)
	private String type;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "配置参数")
	private String param;

}

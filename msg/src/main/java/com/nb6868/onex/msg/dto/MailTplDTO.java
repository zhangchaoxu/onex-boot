package com.nb6868.onex.msg.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息-模板")
public class MailTplDTO extends BaseDTO {

	@ApiModelProperty(value = "编码")
	@NotBlank(message = "编码不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "类型 1验证码 2状态通知 3营销消息")
	@NotNull(message = "类型不能为空", groups = DefaultGroup.class)
	private Integer type;

	@ApiModelProperty(value = "渠道,短信sms/电邮email/微信公众号模板消息wx_mp_template/微信小程序订阅消息wx_ma_subscribe/站内信 notice")
	@NotBlank(message = "渠道不能为空", groups = DefaultGroup.class)
	private String channel;

	@ApiModelProperty(value = "平台 如aliyun/juhe")
	private String platform;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "配置参数")
	private JSONObject params;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

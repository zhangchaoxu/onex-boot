package com.nb6868.onexboot.api.modules.msg.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 消息推送发送请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息推送发送请求")
public class PushSendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "推送类型", required = true)
	private int pushType;

	@ApiModelProperty(value = "收件人别名,多个逗号隔开")
	private String alias;

	@ApiModelProperty(value = "收件人标签,多个逗号隔开")
	private String tags;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "参数")
	private String params;

	@ApiModelProperty(value = "Apns生产环境")
	private Boolean apnsProd;

}

package com.nb6868.onex.api.modules.msg.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息推送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息推送记录")
public class PushLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "推送类型，10全部 20按alias 30按tag 40按alias和tag的合集")
	private Integer type;

	@ApiModelProperty(value = "推送人alias")
	private String alias;

	@ApiModelProperty(value = "推送人tag")
	private String tags;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "参数")
	private String params;

	@ApiModelProperty(value = "发送结果")
	private String result;

	@ApiModelProperty(value = "发送状态  0：失败  1：成功")
	private Integer state;

}

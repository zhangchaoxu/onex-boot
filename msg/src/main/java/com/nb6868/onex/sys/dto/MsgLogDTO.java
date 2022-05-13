package com.nb6868.onex.sys.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息-记录")
public class MsgLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "模板编码")
	private String tplCode;

	@ApiModelProperty(value = "验证码")
	private String code;

	@ApiModelProperty(value = "有效期结束")
	private Date validEndTime;

	@ApiModelProperty(value = "发送者")
	private String mailFrom;

	@ApiModelProperty(value = "收件人")
	private String mailTo;

	@ApiModelProperty(value = "抄送")
	private String mailCc;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "内容参数")
	private JSONObject contentParams;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "消费状态")
	private Integer consumeState;

	@ApiModelProperty(value = "发送状态")
	private Integer state;

	@ApiModelProperty(value = "发送结果")
	private String result;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

}

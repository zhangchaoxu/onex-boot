package com.nb6868.onex.msg.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "消息-记录")
public class MsgLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	 @Schema(description = "模板编码")
	private String tplCode;

	 @Schema(description = "验证码")
	private String code;

	 @Schema(description = "有效期结束")
	private Date validEndTime;

	 @Schema(description = "发送者")
	private String mailFrom;

	 @Schema(description = "收件人")
	private String mailTo;

	 @Schema(description = "抄送")
	private String mailCc;

	 @Schema(description = "标题")
	private String title;

	 @Schema(description = "内容参数")
	private JSONObject contentParams;

	 @Schema(description = "内容")
	private String content;

	 @Schema(description = "消费状态")
	private Integer consumeState;

	 @Schema(description = "发送状态")
	private Integer state;

	 @Schema(description = "发送结果")
	private String result;

	 @Schema(description = "租户编码")
	private String tenantCode;

}

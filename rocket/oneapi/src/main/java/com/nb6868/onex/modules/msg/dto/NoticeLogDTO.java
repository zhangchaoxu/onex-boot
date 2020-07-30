package com.nb6868.onex.modules.msg.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知发送记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "通知发送记录")
public class NoticeLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "模板ID")
	private Long tplId;

	@ApiModelProperty(value = "模板编码")
	private String tplCode;

	@ApiModelProperty(value = "收件人id")
	private Long userId;

	@ApiModelProperty(value = "表主键")
	private Long tableId;

	@ApiModelProperty(value = "表名")
	private String tableName;

	@ApiModelProperty(value = "标题")
	private String subject;

	@ApiModelProperty(value = "正文")
	private String content;

	@ApiModelProperty(value = "发送状态  0:失败  1: 成功")
	private Integer status;

	@ApiModelProperty(value = "是否已读  0:未读 1: 已读")
	private Integer readed;

}

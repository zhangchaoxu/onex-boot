package com.nb6868.onexboot.api.modules.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * CRM商机记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CRM商机记录")
public class BusinessLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

	@ApiModelProperty(value = "客户id")
	private Long customerId;

	@ApiModelProperty(value = "客户名称")
	private String customerName;

	@ApiModelProperty(value = "商机id")
	@NotNull(message = "商机不能为空", groups = DefaultGroup.class)
	private Long businessId;

	@ApiModelProperty(value = "记录类型 followup跟进 new创建 edit修改 close关闭")
	private String type;

	@ApiModelProperty(value = "状态1 阶段1 2 阶段2 3 阶段3 10 赢单 -10 输单 0 无效")
	@Range(min = -10, max = 10, message = "状态取值异常", groups = DefaultGroup.class)
	private Integer state;

	@ApiModelProperty(value = "记录时间，比如跟进时间")
	@NotNull(message = "跟进时间不能为空", groups = DefaultGroup.class)
	private Date logDate;

	@ApiModelProperty(value = "下次跟进时间")
	private Date nextFollowDate;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "附件")
	private String attachment;



}

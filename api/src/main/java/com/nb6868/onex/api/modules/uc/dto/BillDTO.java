package com.nb6868.onex.api.modules.uc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "账单流水")
public class BillDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户id")
	private Long userId;

	@ApiModelProperty(value = "用户姓名")
	private String userName;

	@ApiModelProperty(value = "操作类型")
	private Integer optType;

	@ApiModelProperty(value = "类型 balance/income/points")
	private String type;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "操作金额")
	private BigDecimal amount;

	@ApiModelProperty(value = "账户余额")
	private BigDecimal balance;

	@ApiModelProperty(value = "收入余额")
	private BigDecimal income;

	@ApiModelProperty(value = "积分余额")
	private BigDecimal points;

	@ApiModelProperty(value = "相关订单")
	private Long orderId;

	@ApiModelProperty(value = "状态  0：异常   1：正常")
	private Integer state;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

}

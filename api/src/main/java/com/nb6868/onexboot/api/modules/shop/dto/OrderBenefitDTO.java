package com.nb6868.onexboot.api.modules.shop.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单收益明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单收益明细")
public class OrderBenefitDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单id")
	private Long orderId;

	@ApiModelProperty(value = "订单明细id")
	private Long orderItemId;

	@ApiModelProperty(value = "商品id")
	private Long goodsId;

	@ApiModelProperty(value = "租户id")
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	private String tenantName;

	@ApiModelProperty(value = "收益用户id")
	private Long benefitUserId;

	@ApiModelProperty(value = "收益金额")
	private BigDecimal benefitPrice;

	@ApiModelProperty(value = "收益状态 0 未发放 1 已发放 -1 已回收")
	private Integer benefitStatus;

	@ApiModelProperty(value = "支付订单id")
	private Long payOrderId;

}

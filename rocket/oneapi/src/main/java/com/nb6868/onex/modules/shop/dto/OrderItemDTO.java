package com.nb6868.onex.modules.shop.dto;

import com.nb6868.onex.booster.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单明细")
public class OrderItemDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单id")
	private Long orderId;

	@ApiModelProperty(value = "商品id")
	private Long goodsId;

	@ApiModelProperty(value = "商品数量")
	private BigDecimal goodsQty;

	@ApiModelProperty(value = "商品单价")
	private BigDecimal goodsPrice;

	@ApiModelProperty(value = "商品折扣价")
	private BigDecimal goodsDiscountPrice;

	@ApiModelProperty(value = "商品名称,冗余")
	private String goodsName;

	@ApiModelProperty(value = "商品封面,冗余")
	private String goodsCover;

	@ApiModelProperty(value = "商品总价")
	private BigDecimal goodTotalPrice;

	@ApiModelProperty(value = "商品折扣总价")
	private BigDecimal goodsTotalDiscountPrice;

	@ApiModelProperty(value = "状态0 正常 -1已退款")
	private Integer status;

	@ApiModelProperty(value = "收益用户id")
	private Long benefitUserId;

	@ApiModelProperty(value = "收益金额")
	private BigDecimal benefitPrice;

	@ApiModelProperty(value = "收益状态 0 未发放 1 已发放 -1 已回收")
	private Integer benefitStatus;

}

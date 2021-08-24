package com.nb6868.onex.api.modules.shop.dto;

import com.nb6868.onex.api.modules.shop.ShopConst;
import com.nb6868.onex.common.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单")
public class OrderDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "状态 0：待付款  1：待发货  2:待收货  3：待评价  -1：退款   -2：售后")
	private Integer state;

	@ApiModelProperty(value = "支付状态 0 待支付 1已支付 -1已退款")
	private Integer payState;

	@ApiModelProperty(value = "订单号")
	private String no;

	@ApiModelProperty(value = "购买方式")
	private String buyType;

	@ApiModelProperty(value = "买方用户id")
	private Long userId;

	@ApiModelProperty(value = "卖方用户id")
	private Long sellUserId;

	@ApiModelProperty(value = "用户备注")
	private String userRemark;

	@ApiModelProperty(value = "商品详情,简略商品信息")
	private String goodsDetail;

	@ApiModelProperty(value = "商品费用")
	private BigDecimal goodsPrice;

	@ApiModelProperty(value = "物流费用")
	private BigDecimal expressPrice;

	@ApiModelProperty(value = "商品折扣费用")
	private BigDecimal goodsDiscountPrice;

	@ApiModelProperty(value = "订单总费用")
	private BigDecimal totalPrice;

	@ApiModelProperty(value = "积分抵扣")
	private Integer pointsDeduct;

	@ApiModelProperty(value = "积分抵扣金额")
	private BigDecimal pointPrice;

	@ApiModelProperty(value = "支付费用")
	private BigDecimal payPrice;

	@ApiModelProperty(value = "支付类型 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付 5 账户余额支付")
	private Integer payType;

	@ApiModelProperty(value = "下单时间")
	private Date orderTime;

	@ApiModelProperty(value = "支付交易ID")
	private String payTransactionId;

	@ApiModelProperty(value = "付款时间")
	private Date payTime;

	@ApiModelProperty(value = "发货时间")
	private Date deliverTime;

	@ApiModelProperty(value = "物流单号")
	private String expressNo;

	@ApiModelProperty(value = "物流类型")
	private String expressType;

	@ApiModelProperty(value = "收货地址id")
	private Long receiverId;

	@ApiModelProperty(value = "收件人")
	private String receiverConsignee;

	@ApiModelProperty(value = "收件人电话")
	private String receiverMobile;

	@ApiModelProperty(value = "收件详细地址")
	private String receiverAddress;

	@ApiModelProperty(value = "收件地址区域编码")
	private String receiverRegionCode;

	@ApiModelProperty(value = "收件地址区域")
	private String receiverRegionName;

	@ApiModelProperty(value = "收件地址纬度")
	private Double receiverRegionLat;

	@ApiModelProperty(value = "收件地址经度")
	private String receiverRegionLng;

	@ApiModelProperty(value = "商品列表")
	private List<OrderItemDTO> orderItemList;

	/**
	 * 系统
	 * 订单是否可取消
	 */
	public boolean isSysCancelable() {
		return state == ShopConst.OrderStateEnum.PLACED.value();
	}

	/**
	 * 用户
	 * 订单是否可取消
	 */
	public boolean isUserCancelable() {
		return state == ShopConst.OrderStateEnum.PLACED.value();
	}

	/**
	 * 是否可支付
	 * 订单状态处于未支付,并且时间允许
	 */
	public boolean isPayable() {
		return state == ShopConst.OrderStateEnum.PLACED.value();
	}

}

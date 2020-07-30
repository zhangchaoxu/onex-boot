package com.nb6868.onex.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseEntity;
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
@TableName("shop_order_benefit")
public class OrderBenefitEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
	private Long orderId;
    /**
     * 订单明细id
     */
	private Long orderItemId;
    /**
     * 商品id
     */
	private Long goodsId;
    /**
     * 租户id
     */
	private Long tenantId;
    /**
     * 租户名称
     */
	private String tenantName;
    /**
     * 收益用户id
     */
	private Long benefitUserId;
    /**
     * 收益金额
     */
	private BigDecimal benefitPrice;
    /**
     * 收益状态 0 未发放 1 已发放 -1 已回收
     */
	private Integer benefitStatus;
    /**
     * 支付订单id
     */
	private Long payOrderId;
}

package com.nb6868.onexboot.api.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseTenantEntity;
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
@TableName("shop_order_item")
public class OrderItemEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 商品数量
     */
    private BigDecimal goodsQty;
    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;
    /**
     * 商品折扣价
     */
    private BigDecimal goodsDiscountPrice;
    /**
     * 商品名称,冗余
     */
    private String goodsName;
    /**
     * 商品封面,冗余
     */
    private String goodsCover;
    /**
     * 商品总价
     */
    private BigDecimal goodTotalPrice;
    /**
     * 商品折扣总价
     */
    private BigDecimal goodsTotalDiscountPrice;
    /**
     * 状态0 正常 -1已退款
     */
    private Integer state;
}

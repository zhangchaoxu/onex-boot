package com.nb6868.onex.shop.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_cart")
public class CartEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 会员id
     */
    private Long userId;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 数量
     */
    private BigDecimal qty;
    /**
     * 状态0 未下单 1 已下单
     */
    private Integer state;
    /**
     * 勾选状态0 未勾选 1 已勾选
     */
    private Integer checked;
}

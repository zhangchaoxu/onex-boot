package com.nb6868.onexboot.api.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_coupon")
public class CouponEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;
    /**
     * 名称
     */
	private String name;
    /**
     * 描述
     */
	private String content;
    /**
     * 类型,1 满减券
     */
	private Integer type;
    /**
     * 有效期开始
     */
	private Date validStartTime;
    /**
     * 有效期结束
     */
	private Date validEndTime;
    /**
     * 发放方式, 1注册赠送 2积分兑换 3用户领取
     */
	private Integer giveType;
    /**
     * 状态 0 未激活 1 已激活
     */
	private Integer state;
    /**
     * 兑换积分
     */
	private Integer pointExchange;
    /**
     * 发放总量
     */
	private Integer totalQty;
    /**
     * 用户领取限制
     */
	private Integer userQtyLimit;
    /**
     * 已发放量
     */
	private Integer giveQty;
    /**
     * 限制商品类别
     */
	private Long limitGoodsCategoryId;
    /**
     * 限制商品
     */
	private Long limitGoodsId;
    /**
     * 达标价格
     */
	private BigDecimal limitPrice;
    /**
     * 减免价格
     */
	private BigDecimal reducedPrice;
    /**
     * 价格计算表达式
     */
	private String priceExpress;
}

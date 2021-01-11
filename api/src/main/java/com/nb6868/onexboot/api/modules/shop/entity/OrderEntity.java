package com.nb6868.onexboot.api.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseTenantEntity;
import com.nb6868.onexboot.common.util.DateUtils;
import com.nb6868.onexboot.api.modules.shop.ShopConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_order")
@Alias("shop_order")
public class OrderEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 状态 0：待付款  1：待发货  2:待收货  3：待评价  -1：退款   -2：售后
     */
    private Integer status;
    /**
     * 支付状态 0 待支付 1已支付 -1已退款
     */
    private Integer payStatus;
    /**
     * 订单号
     */
    private String no;
    /**
     * 购买方式
     */
    private String buyType;
    /**
     * 买方用户id
     */
    private Long userId;
    /**
     * 卖方用户id
     */
    private Long sellUserId;
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
     * 用户备注
     */
    private String userRemark;
    /**
     * 商品详情,简略商品信息
     */
    private String goodsDetail;
    /**
     * 商品费用
     */
    private BigDecimal goodsPrice;
    /**
     * 物流费用
     */
    private BigDecimal expressPrice;
    /**
     * 商品折扣费用
     */
    private BigDecimal goodsDiscountPrice;
    /**
     * 订单总费用
     */
    private BigDecimal totalPrice;
    /**
     * 积分抵扣
     */
    private Integer pointsDeduct;
    /**
     * 积分抵扣金额
     */
    private BigDecimal pointPrice;
    /**
     * 支付费用
     */
    private BigDecimal payPrice;
    /**
     * 支付类型 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付 5 账户余额支付
     */
    private Integer payType;
    /**
     * 下单时间
     */
    private Date orderTime;
    /**
     * 支付交易ID
     */
    private String payTransactionId;
    /**
     * 付款时间
     */
    private Date payTime;
    /**
     * 发货时间
     */
    private Date deliverTime;
    /**
     * 物流单号
     */
    private String expressNo;
    /**
     * 物流类型
     */
    private String expressType;
    /**
     * 收货地址id
     */
    private Long receiverId;
    /**
     * 收件人
     */
    private String receiverConsignee;
    /**
     * 收件人电话
     */
    private String receiverMobile;
    /**
     * 收件详细地址
     */
    private String receiverAddress;
    /**
     * 收件地址区域编码
     */
    private String receiverRegionCode;
    /**
     * 收件地址区域
     */
    private String receiverRegionName;
    /**
     * 收件地址纬度
     */
    private Double receiverRegionLat;
    /**
     * 收件地址经度
     */
    private String receiverRegionLng;

    /**
     * 系统
     * 订单是否可取消
     */
    public boolean isSysCancelable() {
        return status == ShopConst.OrderStatusEnum.PLACED.value();
    }

    /**
     * 系统是否可退款
     */
    public boolean isSysRefundable() {
        // 订单已支付,并且订单时间未超90天
        return payStatus == ShopConst.OrderPayStatusEnum.PAID.value() && DateUtils.addDateDays(orderTime, 90).after(DateUtils.now());
    }

    /**
     * 用户
     * 订单是否可取消
     */
    public boolean isUserCancelable() {
        return status == ShopConst.OrderStatusEnum.PLACED.value();
    }
}

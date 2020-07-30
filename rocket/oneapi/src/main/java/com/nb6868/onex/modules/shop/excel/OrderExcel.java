package com.nb6868.onex.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderExcel {
    @Excel(name = "状态 0：待付款  1：待发货  2:待收货  3：待评价  -1：退款   -2：售后")
    private Integer status;
    @Excel(name = "订单号")
    private String no;
    @Excel(name = "用户id")
    private Long userId;
    @Excel(name = "用户备注")
    private String userRemark;
    @Excel(name = "商品费用")
    private BigDecimal goodsPrice;
    @Excel(name = "订单费用")
    private BigDecimal price;
    @Excel(name = "实付费用")
    private BigDecimal payPrice;
    @Excel(name = "支付ID")
    private String prepayId;
    @Excel(name = "支付类型 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付")
    private Integer payType;
    @Excel(name = "付款时间")
    private Date payTime;
    @Excel(name = "物流单号")
    private String expressNo;
    @Excel(name = "物流类型")
    private String expressType;
    @Excel(name = "物流费用")
    private BigDecimal expressPrice;
    @Excel(name = "收货地址id")
    private Long receiverId;
    @Excel(name = "收件人")
    private String receiverConsignee;
    @Excel(name = "收件人电话")
    private String receiverMobile;
    @Excel(name = "收件详细地址")
    private String receiverAddress;
    @Excel(name = "收件地址区域编码")
    private String receiverRegionCode;
    @Excel(name = "收件地址区域")
    private String receiverRegionName;

}

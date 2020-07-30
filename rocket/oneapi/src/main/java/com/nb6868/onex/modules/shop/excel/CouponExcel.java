package com.nb6868.onex.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class CouponExcel {

    @Excel(name = "名称")
    private String name;
    @Excel(name = "描述")
    private String content;
    @Excel(name = "类型,1 满减券")
    private Integer type;
    @Excel(name = "有效期开始")
    private Date validStartTime;
    @Excel(name = "有效期结束")
    private Date validEndTime;
    @Excel(name = "发放方式, 1注册赠送 2积分兑换 3用户领取")
    private Integer giveType;
    @Excel(name = "状态 0 未激活 1 已激活")
    private Integer status;
    @Excel(name = "兑换积分")
    private Integer pointExchange;
    @Excel(name = "发放总量")
    private Integer totalQty;
    @Excel(name = "用户领取限制")
    private Integer userQtyLimit;
    @Excel(name = "已发放量")
    private Integer giveQty;
    @Excel(name = "达标价格")
    private BigDecimal limitPrice;
    @Excel(name = "减免价格")
    private BigDecimal reducedPrice;
    @Excel(name = "价格计算表达式")
    private String priceExpress;

}

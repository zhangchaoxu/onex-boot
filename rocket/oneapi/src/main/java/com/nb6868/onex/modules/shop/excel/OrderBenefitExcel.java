package com.nb6868.onex.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单收益明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderBenefitExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "创建者")
    private Long createId;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "更新者")
    private Long updateId;
    @Excel(name = "更新时间")
    private Date updateTime;
    @Excel(name = "删除标记")
    private Integer deleted;
    @Excel(name = "订单id")
    private Long orderId;
    @Excel(name = "订单明细id")
    private Long orderItemId;
    @Excel(name = "商品id")
    private Long goodsId;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;
    @Excel(name = "收益用户id")
    private Long benefitUserId;
    @Excel(name = "收益金额")
    private BigDecimal benefitPrice;
    @Excel(name = "收益状态 0 未发放 1 已发放 -1 已回收")
    private Integer benefitStatus;
    @Excel(name = "支付订单id")
    private Long payOrderId;

}

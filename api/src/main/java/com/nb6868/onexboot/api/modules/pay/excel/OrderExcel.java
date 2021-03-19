package com.nb6868.onexboot.api.modules.pay.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "渠道id")
    private Long channelId;
    @Excel(name = "支付订单号")
    private String transactionId;
    @Excel(name = "订单id")
    private Long orderId;
    @Excel(name = "订单号")
    private String orderNo;
    @Excel(name = "订单金额")
    private Long totalFee;
    @Excel(name = "支付完成时间")
    private Date endTime;
    @Excel(name = "支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成")
    private Integer state;
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
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;

}

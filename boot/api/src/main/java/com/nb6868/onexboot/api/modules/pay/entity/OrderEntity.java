package com.nb6868.onexboot.api.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pay_order")
@Alias("pay_order")
public class OrderEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 渠道id
     */
    private Long channelId;
    /**
     * 渠道支付参数
     */
    private String channelParam;
    /**
     * 支付订单号
     */
    private String transactionId;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单表名,如shop_order
     */
    private String orderTable;
    /**
     * 通知地址
     */
    private String notifyUrl;
    /**
     * 通知次数
     */
    private Integer notifyCount;
    /**
     * 订单金额
     */
    private Integer totalFee;
    /**
     * 支付完成时间
     */
    private Date endTime;
    /**
     * 支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成
     */
    private Integer status;

}

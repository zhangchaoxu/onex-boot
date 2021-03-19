package com.nb6868.onexboot.api.modules.pay.dto;

import com.nb6868.onexboot.common.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "支付订单")
public class OrderDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "渠道id")
	private Long channelId;

	@ApiModelProperty(value = "渠道支付参数")
	private String channelParam;

	@ApiModelProperty(value = "支付订单号")
	private String transactionId;

	@ApiModelProperty(value = "订单id")
	private Long orderId;

	@ApiModelProperty(value = "订单号")
	private String orderNo;

	@ApiModelProperty(value = "订单表名,如shop_order")
	private String orderTable;

	@ApiModelProperty(value = "订单金额")
	private Integer totalFee;

	@ApiModelProperty(value = "支付完成时间")
	private Date endTime;

	@ApiModelProperty(value = "支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成")
	private Integer state;

	@ApiModelProperty(value = "通知地址")
	private String notifyUrl;

	@ApiModelProperty(value = "通知次数")
	private Integer notifyCount;

}

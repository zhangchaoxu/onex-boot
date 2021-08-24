package com.nb6868.onex.api.modules.shop.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单")
public class OrderPlaceRequest extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户id", required = true)
	@NotNull(message = "用户不能为空", groups = DefaultGroup.class)
	private Long userId;

	@ApiModelProperty(value = "用户备注")
	private String userRemark;

	@ApiModelProperty(value = "支付类型 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付")
	private Integer payType;

	@ApiModelProperty(value = "收货地址id")
	private Long receiverId;

	@ApiModelProperty(value = "收件人", required = true)
	@NotBlank(message = "收件人不能为空", groups = DefaultGroup.class)
	private String receiverConsignee;

	@ApiModelProperty(value = "收件人电话", required = true)
	@NotBlank(message = "收件人电话不能为空", groups = DefaultGroup.class)
	private String receiverMobile;

	@ApiModelProperty(value = "收件详细地址", required = true)
	@NotBlank(message = "收件详细地址不能为空", groups = DefaultGroup.class)
	private String receiverAddress;

	@ApiModelProperty(value = "收件地址区域编码")
	private String receiverRegionCode;

	@ApiModelProperty(value = "收件地址区域", required = true)
	@NotBlank(message = "收件地址区域不能为空", groups = DefaultGroup.class)
	private String receiverRegionName;

	@ApiModelProperty(value = "商品明细")
	private List<OrderItemPlaceRequest> items;

}

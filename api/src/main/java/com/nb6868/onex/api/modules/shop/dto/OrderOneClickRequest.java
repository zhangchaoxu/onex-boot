package com.nb6868.onex.api.modules.shop.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 一键下单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "一键下单")
public class OrderOneClickRequest extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "支付参数编码", required = true)
	@NotBlank(message = "支付参数编码不能为空", groups = DefaultGroup.class)
	private String paramCode;

	@ApiModelProperty(value = "用户备注")
	private String userRemark;

	@ApiModelProperty(value = "支付类型 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付 5 余额支付")
	@Range(min = 0, max = 5, message = "支付类型0-5", groups = DefaultGroup.class)
	private Integer payType;

	@ApiModelProperty(value = "收货地址id")
	private Long receiverId;

	@ApiModelProperty(value = "收件人", required = true)
	@NotBlank(message = "收件人不能为空", groups = DefaultGroup.class)
	private String receiverConsignee;

	@ApiModelProperty(value = "收件人电话", required = true)
	@Pattern(regexp = "^(1)[0-9]{10}$", message = "联系电话格式错误", groups = DefaultGroup.class)
	private String receiverMobile;

	@ApiModelProperty(value = "收件详细地址", required = true)
	private String receiverAddress;

	@ApiModelProperty(value = "收件地址区域编码")
	private String receiverRegionCode;

	@ApiModelProperty(value = "收件地址区域")
	private String receiverRegionName;

	@ApiModelProperty(value = "商品数量", required = true)
	@Min(value = 0, message = "商品数量不能为空", groups = DefaultGroup.class)
	private BigDecimal qty;

	@ApiModelProperty(value = "商品id", required = true)
	@NotNull(message = "商品id不能为空", groups = DefaultGroup.class)
	private Long goodsId;

}

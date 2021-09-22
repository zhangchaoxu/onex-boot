package com.nb6868.onex.shop.modules.shop.dto;

import com.nb6868.onex.common.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 购物车
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "购物车")
public class CartDTO extends BaseTenantDTO {

	@ApiModelProperty(value = "会员id")
	private Long userId;

	@ApiModelProperty(value = "商品id")
	private Long goodsId;

	@ApiModelProperty(value = "数量")
	private BigDecimal qty;

	@ApiModelProperty(value = "状态0 未下单 1 已下单")
	private Integer state;

	@ApiModelProperty(value = "勾选状态0 未勾选 1 已勾选")
	private Integer checked;

}

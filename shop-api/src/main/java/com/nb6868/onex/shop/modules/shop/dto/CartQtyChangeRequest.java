package com.nb6868.onex.shop.modules.shop.dto;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "购物车商品数量修改请求")
public class CartQtyChangeRequest implements Serializable {

	@ApiModelProperty(value = "商品id", required = true)
	@NotNull(message = "商品id不能为空", groups = DefaultGroup.class)
	private Long goodsId;

	@ApiModelProperty(value = "数量", required = true)
	@NotNull(message = "商品数量不能为空", groups = DefaultGroup.class)
	private BigDecimal qty;

}

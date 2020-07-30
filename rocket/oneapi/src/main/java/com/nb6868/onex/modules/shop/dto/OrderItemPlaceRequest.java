package com.nb6868.onex.modules.shop.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "订单")
public class OrderItemPlaceRequest extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "商品Id")
	private Long goodsId;

	@ApiModelProperty(value = "数量")
	private BigDecimal qty;

}

package com.nb6868.onexboot.api.modules.shop.dto;

import com.nb6868.onexboot.common.pojo.BaseTenantDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 出入库记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "出入库记录")
public class StockLogDTO extends BaseTenantDTO {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "创建人姓名")
	private String createName;

	@ApiModelProperty(value = "商品id", required = true)
	@NotNull(message = "商品id不能为空", groups = DefaultGroup.class)
	private Long goodsId;

	@ApiModelProperty(value = "商品名称")
	private String goodsName;

	@ApiModelProperty(value = "类型", required = true)
	@Range(min = 0, max = 1, message = "类型取值0-1", groups = DefaultGroup.class)
	private Integer type;

	@ApiModelProperty(value = "入库数量")
	@Range(min = 0, max = 99999, message = "入库数量取值0-99999", groups = DefaultGroup.class)
	private BigDecimal inQty;

	@ApiModelProperty(value = "出库数量")
	@Range(min = 0, max = 99999, message = "出库数量取值0-99999", groups = DefaultGroup.class)
	private BigDecimal outQty;

	@ApiModelProperty(value = "出入库后库存")
	private BigDecimal stock;

	@ApiModelProperty(value = "备注")
	private String remark;

}

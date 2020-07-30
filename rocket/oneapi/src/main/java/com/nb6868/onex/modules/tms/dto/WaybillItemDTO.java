package com.nb6868.onex.modules.tms.dto;

import com.nb6868.onex.booster.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * TMS-运单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TMS-运单明细")
public class WaybillItemDTO extends BaseTenantDTO {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "供应商id")
	private Long supplierId;

	@ApiModelProperty(value = "供应商名称")
	private String supplierName;

	@ApiModelProperty(value = "箱号")
	private String code;

	@ApiModelProperty(value = "封号")
	private String sealCode;

	@ApiModelProperty(value = "货名")
	private String goods;

	@ApiModelProperty(value = "品种")
	private String goodsType;

	@ApiModelProperty(value = "单位")
	private String unit;

	@ApiModelProperty(value = "数量")
	private BigDecimal qty;

	@ApiModelProperty(value = "卸货数量")
	private BigDecimal qtyUnload;

	@ApiModelProperty(value = "单价")
	private BigDecimal price;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "创建者名字")
	private String createName;

	@ApiModelProperty(value = "运单id")
	private Long waybillId;

	@ApiModelProperty(value = "运单号")
	private String waybillCode;

	@ApiModelProperty(value = "进货时间")
	private String purchaseDate;
}

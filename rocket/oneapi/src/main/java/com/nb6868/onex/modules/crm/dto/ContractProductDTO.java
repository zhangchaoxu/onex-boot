package com.nb6868.onex.modules.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * CRM合同-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CRM合同-产品明细")
public class ContractProductDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "客户id")
	private Long customerId;

	@ApiModelProperty(value = "合同id")
	private Long contractId;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

	@ApiModelProperty(value = "产品id")
	private Long productId;

	@ApiModelProperty(value = "产品名称")
	private String productName;

	@ApiModelProperty(value = "产品单位")
	private String productUnit;

	@ApiModelProperty(value = "产品分类id")
	private Long productCategoryId;

	@ApiModelProperty(value = "产品分类名称")
	private String productCategoryName;

	@ApiModelProperty(value = "数量")
	private BigDecimal qty;

	@ApiModelProperty(value = "产品标准价格")
	private BigDecimal salePrice;

	@ApiModelProperty(value = "折扣")
	private BigDecimal discount;

	@ApiModelProperty(value = "产品折扣价格")
	private BigDecimal discountPrice;

	@ApiModelProperty(value = "价格小计")
	private BigDecimal totalPrice;

}

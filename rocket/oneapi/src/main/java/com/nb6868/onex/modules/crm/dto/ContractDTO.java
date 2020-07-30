package com.nb6868.onex.modules.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.booster.pojo.BaseDTO;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CRM合同")
public class ContractDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "客户id")
	@NotNull(message = "客户不能为空", groups = DefaultGroup.class)
	private Long customerId;

	@ApiModelProperty(value = "客户名称")
	private String customerName;

	@ApiModelProperty(value = "支付方式")
	private String paytype;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "附件")
	private String attachment;

	@ApiModelProperty(value = "合同签订日期")
	private Date contractDate;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

	@ApiModelProperty(value = "合同名称")
	private String name;

	@ApiModelProperty(value = "合同编号")
	private String code;

	@ApiModelProperty(value = "商机id")
	private Long businessId;

	@ApiModelProperty(value = "商机名称")
	private String businessName;

	@ApiModelProperty(value = "合同金额")
	private BigDecimal amount;

	@ApiModelProperty(value = "下单日期")
	private Date orderDate;

	@ApiModelProperty(value = "合同开始日期")
	private Date validStartDate;

	@ApiModelProperty(value = "合同结束日期")
	private Date validEndDate;

	@ApiModelProperty(value = "客户签约人")
	private String customerSigner;

	@ApiModelProperty(value = "公司签约人")
	private String supplierSigner;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "产品折扣")
	private BigDecimal productDiscount;

	@ApiModelProperty(value = "产品总价")
	private BigDecimal productPrice;

	@ApiModelProperty(value = "关联销售id")
	private Long salesUserId;

	@ApiModelProperty(value = "关联销售")
	private String salesUserName;

	@ApiModelProperty(value = "销售提成")
	private BigDecimal salesPercentageAmount;

	@ApiModelProperty(value = "商品明细")
	private List<ContractProductDTO> productList;

}

package com.nb6868.onex.modules.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.booster.pojo.BaseDTO;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CRM商机")
public class BusinessDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

	@ApiModelProperty(value = "商机来源")
	private String source;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "跟进时间")
	private Date followDate;

	@ApiModelProperty(value = "状态1 阶段1 2 阶段2 3 阶段3 10 赢单 -10 输单 0 无效")
	@Range(min = -10, max = 10, message = "状态取值异常", groups = DefaultGroup.class)
	private Integer status;

	@ApiModelProperty(value = "商机金额")
	private BigDecimal amount;

	@ApiModelProperty(value = "预计成交时间")
	private Date dealDate;

	@ApiModelProperty(value = "产品折扣")
	private BigDecimal productDiscount;

	@ApiModelProperty(value = "产品总价")
	private BigDecimal productPrice;

	@ApiModelProperty(value = "关联销售id")
	private Long salesUserId;

	@ApiModelProperty(value = "关联客户id")
	@NotNull(message = "客户不能为空", groups = DefaultGroup.class)
	private Long customerId;

	@ApiModelProperty(value = "关联客户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String customerName;

	@ApiModelProperty(value = "商品明细")
	private List<BusinessProductDTO> productList;

	@ApiModelProperty(value = "销售员")
	private String salesUserName;

}

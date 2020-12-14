package com.nb6868.onexboot.api.modules.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onexboot.common.pojo.BaseDTO;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CRM产品")
public class ProductDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "产品名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "产品编码")
	private String sn;

	@ApiModelProperty(value = "单位")
	private String unit;

	@ApiModelProperty(value = "售价")
	@NotNull(message = "售价不能为空", groups = DefaultGroup.class)
	private BigDecimal salePrice;

	@ApiModelProperty(value = "图文内容")
	private String content;

	@ApiModelProperty(value = "分类id")
	@NotNull(message = "分类不能为空", groups = DefaultGroup.class)
	private Long categoryId;

	@ApiModelProperty(value = "分类名称")
	private String categoryName;

	@ApiModelProperty(value = "是否上架", required = true)
	@Range(min = 0, max = 1, message = "是否上架取值0-1", groups = DefaultGroup.class)
	private Integer marketable;

	@ApiModelProperty(value = "租户id")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String tenantName;

}

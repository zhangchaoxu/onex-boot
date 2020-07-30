package com.nb6868.onex.modules.tms.dto;

import com.nb6868.onex.booster.pojo.BaseTenantDTO;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * TMS-运单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TMS-运单")
public class WaybillDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "运单号")
	@NotBlank(message = "运单号不能为空", groups = DefaultGroup.class)
	private String code;

	@ApiModelProperty(value = "发货人")
	private String sender;

	@ApiModelProperty(value = "运输载体主人")
	private String carrierOwner;

	@ApiModelProperty(value = "运输载体名称")
	private String carrierName;

	@ApiModelProperty(value = "运输载体次号")
	private String carrierNo;

	@ApiModelProperty(value = "出发日期")
	private Date carrierFromDate;

	@ApiModelProperty(value = "出发地")
	private String carrierFrom;

	@ApiModelProperty(value = "目的地")
	private String carrierTo;

	@ApiModelProperty(value = "到达日期")
	private Date carrierToDate;

	@ApiModelProperty(value = "运费类型, 1 包邮、2 明细", required = true)
	@Range(min = 1, max = 2, message = "运费类型取值1-2", groups = DefaultGroup.class)
	private Integer priceType;

	@ApiModelProperty(value = "总价")
	private BigDecimal priceTotal;

	private BigDecimal price1;
	private BigDecimal price2;
	private BigDecimal price3;
	private BigDecimal price4;
	private BigDecimal price5;
	private BigDecimal price6;
	private BigDecimal price7;
	private BigDecimal price8;
	private BigDecimal price9;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "创建者名字")
	private String createName;

	@ApiModelProperty(value = "明细列表")
	private List<WaybillItemDTO> waybillItemList;

	@ApiModelProperty(value = "内容数量")
	private Integer waybillItemCount;

}

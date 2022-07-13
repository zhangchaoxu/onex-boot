package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
/**
 * 用户中心-账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户中心-账单流水")
public class BillDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("用户姓名")
	private String userName;

	@ApiModelProperty("账户类型,如balance/income/points")
	private String billType;

	@ApiModelProperty("操作类型，如系统充值、打卡积分等")
	private String type;

	@ApiModelProperty("备注")
	private String remark;

	@ApiModelProperty("数量")
	private BigDecimal amount;

	@ApiModelProperty("操作前数量")
	private BigDecimal amountBefore;

	@ApiModelProperty("操作后数量")
	private BigDecimal amountAfter;

	@ApiModelProperty("关联信息id，比如订单id")
	private Long relationId;

	@ApiModelProperty("状态  0:待处理   1:已处理  -1: 已拒绝")
	private Integer state;

	@ApiModelProperty("租户编码")
	private String tenantCode;

}

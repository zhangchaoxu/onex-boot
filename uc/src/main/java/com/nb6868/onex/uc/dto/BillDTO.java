package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "用户中心-账单流水")
public class BillDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@Schema(description = "用户id")
	private Long userId;

	@Schema(description = "用户姓名")
	private String userName;

	@Schema(description = "账户类型,如balance/income/points")
	private String billType;

	@Schema(description = "操作类型，如系统充值、打卡积分等")
	private String type;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "数量")
	private BigDecimal amount;

	@Schema(description = "操作前数量")
	private BigDecimal amountBefore;

	@Schema(description = "操作后数量")
	private BigDecimal amountAfter;

	@Schema(description = "关联信息id，比如订单id")
	private Long relationId;

	@Schema(description = "状态  0:待处理   1:已处理  -1: 已拒绝")
	private Integer state;

	@Schema(description = "租户编码")
	private String tenantCode;

}

package com.nb6868.onex.modules.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_bill")
public class BillEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
	private Long userId;
    /**
     * 用户姓名
     */
	private String userName;
    /**
     * 操作类型
     */
	private Integer optType;
    /**
     * 类型 balance/income/points
     */
	private String type;
    /**
     * 备注
     */
	private String remark;
    /**
     * 操作金额
     */
	private BigDecimal amount;
    /**
     * 账户余额
     */
	private BigDecimal balance;
    /**
     * 收入余额
     */
	private BigDecimal income;
    /**
     * 积分余额
     */
	private BigDecimal points;
    /**
     * 相关订单
     */
	private Long orderId;
    /**
     * 状态  0：异常   1：正常
     */
	private Integer status;
}

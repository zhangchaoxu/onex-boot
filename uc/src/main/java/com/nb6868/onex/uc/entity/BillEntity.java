package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * 用户中心-账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "uc_bill", autoResultMap = false)
@Alias("uc_bill")
public class BillEntity extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 账户类型,如balance/income/points
     */
    private String billType;
    /**
     * 操作类型，如系统充值、打卡积分等
     */
    private String type;
    /**
     * 备注
     */
    private String remark;
    /**
     * 数量
     */
    private BigDecimal amount;
    /**
     * 操作前数量
     */
    private BigDecimal amountBefore;
    /**
     * 操作后数量
     */
    private BigDecimal amountAfter;
    /**
     * 关联信息id，比如订单id
     */
    private Long relationId;
    /**
     * 状态  0:待处理   1:已处理  -1: 已拒绝
     */
    private Integer state;
    /**
     * 租户编码
     */
    private String tenantCode;
}

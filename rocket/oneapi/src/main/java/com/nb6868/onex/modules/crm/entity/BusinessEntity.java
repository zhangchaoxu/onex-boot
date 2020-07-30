package com.nb6868.onex.modules.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_business")
public class BusinessEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 客户名字
     */
    private String customerName;
    /**
     * 名称
     */
    private String name;
    /**
     * 租户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 租户名称
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantName;
    /**
     * 商机来源
     */
    private String source;
    /**
     * 备注
     */
    private String remark;
    /**
     * 跟进时间
     */
    private Date followDate;
    /**
     * 状态1 阶段1 2 阶段2 3 阶段3 10 赢单 -10 输单 0 无效
     */
    private Integer status;
    /**
     * 商机金额
     */
    private BigDecimal amount;
    /**
     * 预计成交时间
     */
    private Date dealDate;
    /**
     * 产品折扣
     */
    private BigDecimal productDiscount;
    /**
     * 产品总价
     */
    private BigDecimal productPrice;
    /**
     * 关联销售id
     */
    private Long salesUserId;

    /**
     * 销售员名称
     */
    private String salesUserName;
}

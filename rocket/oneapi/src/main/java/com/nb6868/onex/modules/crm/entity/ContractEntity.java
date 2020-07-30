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
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_contract")
public class ContractEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 支付方式
     */
    private String paytype;
    /**
     * 内容
     */
    private String content;
    /**
     * 附件
     */
    private String attachment;
    /**
     * 合同签订日期
     */
    private Date contractDate;
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
     * 合同名称
     */
    private String name;
    /**
     * 合同编号
     */
    private String code;
    /**
     * 商机id
     */
    private Long businessId;
    /**
     * 商机名称
     */
    private String businessName;
    /**
     * 合同金额
     */
    private BigDecimal amount;
    /**
     * 下单日期
     */
    private Date orderDate;
    /**
     * 合同开始日期
     */
    private Date validStartDate;
    /**
     * 合同结束日期
     */
    private Date validEndDate;
    /**
     * 客户签约人
     */
    private String customerSigner;
    /**
     * 公司签约人
     */
    private String supplierSigner;
    /**
     * 备注
     */
    private String remark;
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
     * 关联销售
     */
    private String salesUserName;
    /**
     * 销售提成
     */
    private BigDecimal salesPercentageAmount;
}

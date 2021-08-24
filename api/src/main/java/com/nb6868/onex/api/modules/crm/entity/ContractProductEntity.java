package com.nb6868.onex.api.modules.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * CRM合同-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_contract_product")
public class ContractProductEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 合同id
     */
    private Long contractId;
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
     * 产品id
     */
    private Long productId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品单位
     */
    private String productUnit;
    /**
     * 产品分类id
     */
    private Long productCategoryId;
    /**
     * 产品分类名称
     */
    private String productCategoryName;
    /**
     * 数量
     */
    private BigDecimal qty;
    /**
     * 产品标准价格
     */
    private BigDecimal salePrice;
    /**
     * 折扣
     */
    private BigDecimal discount;
    /**
     * 产品折扣价格
     */
    private BigDecimal discountPrice;
    /**
     * 价格小计
     */
    private BigDecimal totalPrice;
}

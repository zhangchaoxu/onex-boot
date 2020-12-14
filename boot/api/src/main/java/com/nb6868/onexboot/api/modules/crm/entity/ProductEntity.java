package com.nb6868.onexboot.api.modules.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_product")
@Alias("crm_product")
public class ProductEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 产品名称
     */
    private String name;
    /**
     * 产品编码
     */
    private String sn;
    /**
     * 单位
     */
    private String unit;
    /**
     * 售价
     */
    private BigDecimal salePrice;
    /**
     * 图文内容
     */
    private String content;
    /**
     * 分类id
     */
    private Long categoryId;
    /**
     * 产品类别
     */
    private String categoryName;
    /**
     * 是否上架
     */
    private Integer marketable;
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

}

package com.nb6868.onex.modules.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM 产品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("crm_product_category")
public class ProductCategoryEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 父id
     */
    private Long pid;
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
     * 名称
     */
    private String name;
    /**
     * logo
     */
    private String logo;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 介绍
     */
    private String content;
}

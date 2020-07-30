package com.nb6868.onex.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_goods_category")
public class GoodsCategoryEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 父id
     */
	private Long pid;
    /**
     * 店铺编码
     */
	private String storeCode;
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

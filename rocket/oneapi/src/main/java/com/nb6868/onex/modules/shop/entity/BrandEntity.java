package com.nb6868.onex.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_brand")
public class BrandEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图片
     */
    private String imgs;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 品牌介绍
     */
    private String content;
    /**
     * 状态0 未审核 1 已审核
     */
    private Integer status;
}

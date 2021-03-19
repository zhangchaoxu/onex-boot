package com.nb6868.onexboot.api.modules.cms.entity;

import com.nb6868.onexboot.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_article_category")
public class ArticleCategoryEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图片
     */
    private String imgs;

    private Long siteId;

    private String siteCode;

    /**
     * 站点名称
     */
    @TableField(exist = false)
    private String siteName;
}

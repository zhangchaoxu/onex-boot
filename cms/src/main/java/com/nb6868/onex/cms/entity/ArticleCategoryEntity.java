package com.nb6868.onex.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
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
     * 站点id
     */
    private Long siteId;
    /**
     * 站点编码
     */
    private String siteCode;
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 图片
     */
    private String imgs;
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
     * 站点名称
     */
    @TableField(exist = false)
    private String siteName;
}

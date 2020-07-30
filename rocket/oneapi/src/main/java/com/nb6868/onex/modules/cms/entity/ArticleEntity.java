package com.nb6868.onex.modules.cms.entity;

import com.nb6868.onex.booster.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文章
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_article")
public class ArticleEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private String code;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态0 未发布 1 已发布
     */
    private Integer status;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 图片
     */
    private String imgs;
    /**
     * 备注
     */
    private String remark;
    /**
     * 文章标题
     */
    private String name;
    /**
     * 文章来源
     */
    private String sourceName;
    /**
     * 文章来源连接
     */
    private String sourceLink;
    /**
     * 文章作者
     */
    private String author;
    /**
     * 平均评分
     */
    private Float score;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 发布时间
     */
    private Date pubTime;
    /**
     * 有效期开始
     */
    private Date validStartTime;
    /**
     * 有效期结束
     */
    private Date validEndTime;
    /**
     * 文章类别id
     */
    private Long articleCategoryId;
    /**
     * 点击次数
     */
    private Integer hits;
    /**
     * 是否置顶
     */
    private Integer top;

    private Long siteId;

    private String siteCode;
    /**
     * 文章类别
     */
    @TableField(exist = false)
    private String articleCategoryName;

    /**
     * 站点名称
     */
    @TableField(exist = false)
    private String siteName;

}

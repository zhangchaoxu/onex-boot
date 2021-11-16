package com.nb6868.onex.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
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
     * 文章分类编码
     */
    private String articleCategoryCode;
    /**
     * 文章类别id
     */
    private Long articleCategoryId;
    /**
     * 站点编码
     */
    private String siteCode;
    /**
     * 站点id
     */
    private Long siteId;
    /**
     * 编码
     */
    private String code;
    /**
     * SEO描述
     */
    private String seoDescr;
    /**
     * SEO关键词
     */
    private String seoKeywords;
    /**
     * 来源地址
     */
    private String sourceLink;
    /**
     * 来源
     */
    private String sourceName;
    /**
     * 作者
     */
    private String author;
    /**
     * 标签
     */
    private String tags;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态0 未发布 1 已发布
     */
    private Integer state;
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
     * 副标题
     */
    private String subTitle;
    /**
     * 标题
     */
    private String title;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 评论类型 1 不允许评论 2 运行登录用户评论 3 允许游客评论
     */
    private Integer commentType;
    /**
     * 评分
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
     * 是否首页推荐
     */
    private Integer top;
    /**
     * 点击数
     */
    private Long hits;
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

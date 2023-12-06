package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "文章")
public class ArticleDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	 @Schema(description = "文章分类编码")
	private String articleCategoryCode;

	 @Schema(description = "文章类别id")
	private Long articleCategoryId;

	 @Schema(description = "站点编码")
	private String siteCode;

	 @Schema(description = "站点id")
	private Long siteId;

	 @Schema(description = "编码")
	private String code;

	 @Schema(description = "SEO描述")
	private String seoDescr;

	 @Schema(description = "SEO关键词")
	private String seoKeywords;

	 @Schema(description = "来源地址")
	private String sourceLink;

	 @Schema(description = "来源")
	private String sourceName;

	 @Schema(description = "作者")
	private String author;

	 @Schema(description = "标签")
	private String tags;

	 @Schema(description = "排序")
	private Integer sort;

	 @Schema(description = "状态0 未发布 1 已发布")
	private Integer state;

	 @Schema(description = "类型")
	private Integer type;

	 @Schema(description = "图片")
	private String imgs;

	 @Schema(description = "备注")
	private String remark;

	 @Schema(description = "副标题")
	private String subTitle;

	 @Schema(description = "标题")
	private String title;

	 @Schema(description = "摘要")
	private String summary;

	 @Schema(description = "评论类型 1 不允许评论 2 运行登录用户评论 3 允许游客评论")
	private Integer commentType;

	 @Schema(description = "评分")
	private Float score;

	 @Schema(description = "文章内容")
	private String content;

	 @Schema(description = "发布时间")
	private Date pubTime;

	 @Schema(description = "有效期开始")
	private Date validStartTime;

	 @Schema(description = "有效期结束")
	private Date validEndTime;

	 @Schema(description = "是否首页推荐")
	private Integer top;

	 @Schema(description = "点击数")
	private Long hits;

}

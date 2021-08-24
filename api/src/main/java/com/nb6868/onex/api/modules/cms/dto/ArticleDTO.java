package com.nb6868.onex.api.modules.cms.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "文章")
public class ArticleDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "文章分类编码")
	private String articleCategoryCode;

	@ApiModelProperty(value = "文章类别id")
	private Long articleCategoryId;

	@ApiModelProperty(value = "站点编码")
	private String siteCode;

	@ApiModelProperty(value = "站点id")
	private Long siteId;

	@ApiModelProperty(value = "编码")
	private String code;

	@ApiModelProperty(value = "SEO描述")
	private String seoDescr;

	@ApiModelProperty(value = "SEO关键词")
	private String seoKeywords;

	@ApiModelProperty(value = "来源地址")
	private String sourceLink;

	@ApiModelProperty(value = "来源")
	private String sourceName;

	@ApiModelProperty(value = "作者")
	private String author;

	@ApiModelProperty(value = "标签")
	private String tags;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "状态0 未发布 1 已发布")
	private Integer state;

	@ApiModelProperty(value = "类型")
	private Integer type;

	@ApiModelProperty(value = "图片")
	private String imgs;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "副标题")
	private String subTitle;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "摘要")
	private String summary;

	@ApiModelProperty(value = "评论类型 1 不允许评论 2 运行登录用户评论 3 允许游客评论")
	private Integer commentType;

	@ApiModelProperty(value = "评分")
	private Float score;

	@ApiModelProperty(value = "文章内容")
	private String content;

	@ApiModelProperty(value = "发布时间")
	private Date pubTime;

	@ApiModelProperty(value = "有效期开始")
	private Date validStartTime;

	@ApiModelProperty(value = "有效期结束")
	private Date validEndTime;

	@ApiModelProperty(value = "是否首页推荐")
	private Integer top;

	@ApiModelProperty(value = "点击数")
	private Long hits;

}

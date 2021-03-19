package com.nb6868.onexboot.api.modules.cms.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "文章类目")
public class ArticleCategoryDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "编码")
	private String code;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "状态")
	private Integer state;

	@ApiModelProperty(value = "类型")
	private Integer type;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "图片")
	private String imgs;

	@ApiModelProperty(value = "站点名称")
	private Long siteId;

	@ApiModelProperty(value = "站点编码")
	private String siteCode;

	@ApiModelProperty(value = "站点编码")
	private String siteName;

}

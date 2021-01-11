package com.nb6868.onexboot.api.modules.sys.dto;

import com.nb6868.onexboot.common.util.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 行政区域树
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "行政区域树")
public class RegionTreeDTO extends TreeNode<RegionTreeDTO> implements Serializable  {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "区域编码")
	private Long id;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "原始名称")
	private String extName;

	@ApiModelProperty(value = "原始编号")
	private String extId;

	@ApiModelProperty(value = "拼音")
	private String pinyin;

	@ApiModelProperty(value = "拼音前缀")
	private String pinyinPrefix;

	@ApiModelProperty(value = "区号")
	private String code;

	@ApiModelProperty(value = "邮编")
	private String postcode;

	@ApiModelProperty(value = "层级深度")
	private Integer deep;

	@ApiModelProperty(value = "中心点GCJ-02.格式：\"lng lat\" or \"EMPTY\"")
	private String geo;

	@ApiModelProperty(value = "边界坐标点GCJ-02")
	private String polyline;

}

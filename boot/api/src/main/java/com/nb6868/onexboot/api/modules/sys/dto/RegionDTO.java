package com.nb6868.onexboot.api.modules.sys.dto;

import com.nb6868.onexboot.common.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区域
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "行政区域")
public class RegionDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "区域编码")
	private Long id;

	@ApiModelProperty(value = "上级区域编码,0为根目录")
	private Long pid;

	@ApiModelProperty(value = "区域名称")
	private String name;

	@ApiModelProperty(value = "区域邮编")
	private String code;

	@ApiModelProperty(value = "区域级别")
	private Integer level;

	@ApiModelProperty(value = "区域级别名称")
	private String levelName;

	@ApiModelProperty(value = "中心点")
	private String center;

	@ApiModelProperty(value = "边界坐标点")
	private String polyline;

    @ApiModelProperty(value = "子节点数量")
    private Integer childNum;
}

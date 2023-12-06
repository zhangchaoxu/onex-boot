package com.nb6868.onex.sys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "行政区域")
public class RegionPcdt implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "行政区编码")
	private Long adcode;

	@ApiModelProperty(value = "层级深度")
	private Integer deep;

	@ApiModelProperty(value = "省份")
	private String province;

	@ApiModelProperty(value = "城市")
	private String city;

	@ApiModelProperty(value = "区县")
	private String district;

	@ApiModelProperty(value = "乡镇/街道")
	private String township;

}

package com.nb6868.onex.modules.sys.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "OSS")
public class OssDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "文件路径")
	private String url;

	@ApiModelProperty(value = "文件尺寸")
	private Long size;

	@ApiModelProperty(value = "类型")
	private String contentType;

	@ApiModelProperty(value = "文件名")
	private String filename;

}

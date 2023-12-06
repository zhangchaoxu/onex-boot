package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 素材库
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "OSS")
public class OssDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	 @Schema(description = "文件路径")
	private String url;

	 @Schema(description = "文件尺寸")
	private Long size;

	 @Schema(description = "类型")
	private String contentType;

	 @Schema(description = "文件名")
	private String filename;

}

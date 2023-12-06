package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.io.Serializable;

/**
 * 行政区域
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "行政区域")
public class RegionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	 @Schema(description = "id")
	@Null(message = "{id.null}", groups = AddGroup.class)
	@NotNull(message = "{id.require}", groups = UpdateGroup.class)
	private Long id;

	 @Schema(description = "上级区域编号,0为一级")
	private Long pid;

	 @Schema(description = "名称")
	private String name;

	 @Schema(description = "原始名称")
	private String extName;

	 @Schema(description = "原始编号")
	private String extId;

	 @Schema(description = "拼音")
	private String pinyin;

	 @Schema(description = "拼音前缀")
	private String pinyinPrefix;

	 @Schema(description = "区号")
	private String code;

	 @Schema(description = "邮编")
	private String postcode;

	 @Schema(description = "层级深度")
	private Integer deep;

	 @Schema(description = "中心点GCJ-02.格式：\"lng lat\" or \"EMPTY\"")
	private String geo;

	 @Schema(description = "边界坐标点GCJ-02")
	private String polygon;

	 @Schema(description = "是否有下级")
	public boolean getHasChildren() {
		return deep < 3;
	}
}

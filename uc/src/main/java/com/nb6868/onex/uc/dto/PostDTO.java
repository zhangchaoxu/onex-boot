package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "岗位")
public class PostDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	 @Schema(description = "编码")
	@NotBlank(message = "{code.require}", groups = DefaultGroup.class)
	private String code;

	 @Schema(description = "名称")
	@NotBlank(message = "{name.require}", groups = DefaultGroup.class)
	private String name;

	 @Schema(description = "排序")
	@NotNull(message = "排序不能为空")
	private Integer sort;

	 @Schema(description = "备注")
	private String remark;

	 @Schema(description = "租户编码")
	private String tenantCode;

}

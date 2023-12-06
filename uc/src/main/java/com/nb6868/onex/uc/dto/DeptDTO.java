package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 部门
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "部门")
public class DeptDTO extends BaseDTO {

    @Schema(description = "类型")
    @NotNull(message = "类型不能为空", groups = DefaultGroup.class)
    private Integer type;

    @Schema(description = "编码")
    @NotNull(message = "编码不能为空", groups = DefaultGroup.class)
    private String code;

    @Schema(description = "上级编码")
    @NotNull(message = "上级编码不能为空", groups = DefaultGroup.class)
    private String pcode;

    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "部门名称")
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @Schema(description = "排序")
    @Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
    private Integer sort;

    @Schema(description = "上级部门名称")
    private String parentName;

    @Schema(description = "租户编码")
    private String tenantCode;

}

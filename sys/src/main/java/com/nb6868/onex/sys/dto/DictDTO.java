package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "数据字典")
public class DictDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "上级ID，一级为0")
    @NotNull(message = "{pid.require}", groups = DefaultGroup.class)
    private Long pid;

    @Schema(description = "字典类型")
    @NotBlank(message = "{sysdict.type.require}", groups = DefaultGroup.class)
    private String type;

    @Schema(description = "字典名称")
    @NotBlank(message = "{sysdict.name.require}", groups = DefaultGroup.class)
    private String name;

    @Schema(description = "字典值")
    private String value;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    @Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
    private Integer sort;

}

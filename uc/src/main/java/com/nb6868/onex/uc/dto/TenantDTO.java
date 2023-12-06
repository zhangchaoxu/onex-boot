package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "租户")
public class TenantDTO extends BaseDTO {

     @Schema(description = "编码,需唯一")
    @NotBlank(message = "编码不能为空", groups = DefaultGroup.class)
    private String code;

     @Schema(description = "名称")
    @NotBlank(message = "名称不能为空", groups = DefaultGroup.class)
    private String name;

     @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

     @Schema(description = "备注")
    private String remark;

     @Schema(description = "状态")
    @EnumValue(intValues = {0, 1}, message = "状态指定值0和1", groups = DefaultGroup.class)
    private Integer state;

}

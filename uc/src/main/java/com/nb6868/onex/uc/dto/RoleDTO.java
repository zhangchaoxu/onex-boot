package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "角色")
public class RoleDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色编码")
    @Length(max = 50, message = "请限制编码50字以内", groups = DefaultGroup.class)
    private String code;

    @Schema(description = "名称")
    @NotBlank(message = "{name.require}", groups = DefaultGroup.class)
    private String name;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空", groups = DefaultGroup.class)
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIdList;

}

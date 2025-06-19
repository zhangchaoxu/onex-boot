package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.uc.UcConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 角色
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "角色")
public class RoleSaveOrUpdateReq extends BaseIdReq {

    @Schema(description = "角色编码")
    @Length(max = 50, message = "请限制编码50字以内")
    private String code;

    @Schema(description = "名称")
    @NotBlank(message = "{name.require}")
    private String name;

    @Schema(description = "排序")
    @Range(min = 0, max = 99999, message = "排序取值0-99999")
    private Integer sort;

    @Schema(description = "状态")
    @EnumValue(enumClass = UcConst.RoleStateEnum.class, message = "状态传参错误")
    private Integer state;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIdList;

}

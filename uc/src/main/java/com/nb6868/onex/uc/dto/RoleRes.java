package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "角色(简化信息)")
public class RoleRes extends BaseRes {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

}

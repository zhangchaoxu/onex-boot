package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "部门(简化信息)")
public class DeptRes extends BaseRes {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门上级ID")
    private Long pid;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "上级编码")
    private String pcode;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer state;

}

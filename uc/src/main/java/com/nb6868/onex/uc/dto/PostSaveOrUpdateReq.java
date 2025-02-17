package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "岗位提交请求")
public class PostSaveOrUpdateReq extends BaseIdReq {

    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String code;

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户编码")
    private String tenantCode;

}

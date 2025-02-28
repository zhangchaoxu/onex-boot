package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户个人参数更新请求")
public class ProfileParamsSaveOrUpdateReq extends BaseReq {

    @Schema(description = "编码")
    @NotEmpty(message = "编码不能为空")
    private String code;

    @Schema(description = "内容")
    @NotNull(message = "内容不能null")
    private String content;

    @Schema(description = "备注")
    private String remark;

}

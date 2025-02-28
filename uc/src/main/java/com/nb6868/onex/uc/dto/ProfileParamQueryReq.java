package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户个人参数查询请求")
public class ProfileParamQueryReq extends BaseReq {

    @Schema(description = "编码")
    @NotEmpty(message = "编码不能为空")
    private String code;

}

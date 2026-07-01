package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TAC验证码行为轨迹数据请求")
public class TACaptchaTrackReq extends BaseReq {

    @Schema(description = "验证码id")
    @NotEmpty(message = "验证码id不能为空")
    private String id;

    @Schema(description = "验证码行为数据")
    @NotNull(message = "验证码行为数据不能为空")
    private JSONObject data;

}

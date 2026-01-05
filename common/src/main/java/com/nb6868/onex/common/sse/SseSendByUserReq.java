package com.nb6868.onex.common.sse;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "sse发送给用户请求")
public class SseSendByUserReq extends BaseReq {

    @Schema(description = "发送对象")
    @NotNull(message = "发送对象不能为空")
    private Long userId;

    @Schema(description = "消息内容")
    @NotNull(message = "消息内容不能为空")
    private JSONObject messageBody;

}

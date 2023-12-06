package com.nb6868.onex.common.wechat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信扫码登录配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class WechatScanProps {

    @JsonIgnore
    @Schema(description = "secret")
    private String secret;

    @JsonIgnore
    @Schema(description = "cropId")
    private String cropId;

    @Schema(description = "appid")
    private String appid;

    @Schema(description = "callback")
    private String callback;

}

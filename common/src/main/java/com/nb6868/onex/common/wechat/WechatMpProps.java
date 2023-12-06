package com.nb6868.onex.common.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

/**
 * 微信公众号配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex.wechat.mp")
public class WechatMpProps implements Serializable {

    @Schema(description = "配置项")
    private Map<String, Config> configs;

    @Data
    public static class Config {

        @Schema(description = "appId")
        private String appid;

        @Schema(description = "secret")
        private String secret;

        @Schema(description = "token")
        private String token;

        @Schema(description = "EncodingAESKey")
        private String aesKey;

    }

}

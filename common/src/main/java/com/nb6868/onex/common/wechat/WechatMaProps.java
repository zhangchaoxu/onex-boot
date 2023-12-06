package com.nb6868.onex.common.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex.wechat.ma")
public class WechatMaProps implements Serializable {

     @Schema(description = "配置项")
    private Map<String, Config> configs = new HashMap<>();

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

         @Schema(description = "模板消息id")
        private String templateId;

         @Schema(description = "消息格式，XML或者JSON")
        private String msgDataFormat;

    }

}

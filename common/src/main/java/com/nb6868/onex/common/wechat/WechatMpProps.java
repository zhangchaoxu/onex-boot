package com.nb6868.onex.common.wechat;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "配置项")
    private Map<String, Config> configs;

    @Data
    public static class Config {

        @ApiModelProperty(value = "appId")
        private String appid;

        @ApiModelProperty(value = "secret")
        private String secret;

        @ApiModelProperty(value = "token")
        private String token;

        @ApiModelProperty(value = "EncodingAESKey")
        private String aesKey;

    }

}

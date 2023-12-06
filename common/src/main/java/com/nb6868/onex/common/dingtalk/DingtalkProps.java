package com.nb6868.onex.common.dingtalk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

/**
 * 钉钉配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex.dingtalk")
public class DingtalkProps implements Serializable {

     @Schema(description = "配置项")
    private Map<String, Config> configs;

    @Data
    public static class Config {

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

}

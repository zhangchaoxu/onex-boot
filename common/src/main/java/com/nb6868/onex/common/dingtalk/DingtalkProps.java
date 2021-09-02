package com.nb6868.onex.common.dingtalk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nb6868.onex.common.config.YamlPropertySourceFactory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
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
@ConfigurationProperties(prefix = "dingtalk")
@PropertySource(value = "classpath:onex.yml", factory = YamlPropertySourceFactory.class)
public class DingtalkProps implements Serializable {

    @ApiModelProperty(value = "配置项")
    private Map<String, Config> configs;

    @Data
    public static class Config {

        @JsonIgnore
        @ApiModelProperty(value = "secret")
        private String secret;

        @JsonIgnore
        @ApiModelProperty(value = "cropId")
        private String cropId;

        @ApiModelProperty(value = "appid")
        private String appid;

        @ApiModelProperty(value = "callback")
        private String callback;

    }

}

package com.nb6868.onex.common.params;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 参数配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex.params")
public class ParamsProps {

     @Schema(description = "配置项")
    private Map<String, String> configs;

}

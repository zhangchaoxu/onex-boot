package com.nb6868.onexboot.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OneX相关配置文件
 *
 * @author Charles
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex")
public class OnexProperties {

    /**
     * token生成策略
     */
    private String tokenPolicy;


}

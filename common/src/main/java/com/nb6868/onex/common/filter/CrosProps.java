package com.nb6868.onex.common.filter;

import com.nb6868.onex.common.config.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * CROS配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "cros")
@PropertySource(value = "classpath:onex.yml", factory = YamlPropertySourceFactory.class)
public class CrosProps {

    String allowCredentials = "true";
    String allowHeaders = HttpHeaders.CONTENT_TYPE + ",device,Authorization,token,auth-token,jwt-token";
    String allowMethods = "GET,POST,PUT,DELETE,OPTIONS";
    String maxAge = "3600";

}

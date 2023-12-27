package com.nb6868.onex.common.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * CROS配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "onex.cros")
public class CrosProps {

    String allowCredentials = "true";
    String allowHeaders = HttpHeaders.CONTENT_TYPE + ",device,Authorization,token,auth-token,jwt-token,tunnel-token";
    String allowMethods = "GET,POST,PUT,DELETE,OPTIONS";
    String maxAge = "3600";
    String exposeHeaders = "Content-Disposition";

}

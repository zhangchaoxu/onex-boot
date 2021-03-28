package com.nb6868.onexboot.api.common.config;

import com.nb6868.onexboot.common.config.YamlPropertySourceFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * OneX相关配置文件
 *
 * @author Charles
 */
@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "onex")
@PropertySource(value = "classpath:onex.yml", factory = YamlPropertySourceFactory.class)
public class OnexConfigProperties {

    private LoginConfigSource loginConfigSource = LoginConfigSource.PROPS;

    private final OnexConfigProperties.LoginConfigProperties loginConfigAdmin = new LoginConfigProperties();

    @Data
    @NoArgsConstructor
    public static class LoginConfigProperties {

        private boolean multiLogin = false;
        private TokenPolicy tokenPolicy = TokenPolicy.UUID;
        private Long tokenExpire = 604800L;
        private boolean tokenRenewal = true;
        private boolean roleBase = true;
        private boolean permissionBase = true;

    }
}

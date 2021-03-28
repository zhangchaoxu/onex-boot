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
public class OnexProperties {

    /**
     * 登录配置源,支持配置文件和数据库
     */
    private LoginConfigSource loginConfigSource = LoginConfigSource.PROPS;

    /**
     * 后台登录
     */
    private final LoginProperties loginAdminConfig = new LoginProperties();

    @Data
    @NoArgsConstructor
    public static class LoginProperties {

        /**
         * 是否支持多端登录
         * 支持多端登录,表示可以在不同客户端登录,创建token的时候不判断原先的
         * 不支持多端登录,表示同一个帐号只能在一个地方登录,创建token的时候会将原先的token删除
         */
        private boolean multiLogin = false;
        /**
         * token策略,jwt或者uuid
         */
        private TokenPolicy tokenPolicy = TokenPolicy.UUID;
        /**
         * token有效时间
         */
        private Long tokenExpire = 604800L;
        /**
         * token是否自动延期
         */
        private boolean tokenRenewal = true;
        private boolean roleBase = true;
        private boolean permissionBase = true;

    }
}

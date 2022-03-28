package com.nb6868.onex.common.config;

import com.nb6868.onex.common.shiro.ShiroRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shiro配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@ConditionalOnProperty(name = "onex.shiro.enable", havingValue = "true")
@Configuration
@Slf4j
public class ShiroConfig extends BaseShiroConfig {

    @Bean("securityManager")
    public SecurityManager securityManager(ShiroRealm shiroRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

}

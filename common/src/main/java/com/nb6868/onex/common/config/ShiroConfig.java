package com.nb6868.onex.common.config;

import com.nb6868.onex.common.shiro.ShiroJwtRealm;
import com.nb6868.onex.common.shiro.ShiroUuidRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shiro配置
 * <a href="https://shiro.apache.org/spring-boot.html">Integrating Apache Shiro into Spring-Boot Applications</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@ConditionalOnProperty(name = "onex.shiro.enable", havingValue = "true")
@Configuration
@Slf4j
public class ShiroConfig extends BaseShiroConfig {

    @Bean
    public DefaultWebSessionStorageEvaluator sessionStorageEvaluator() {
        DefaultWebSessionStorageEvaluator evaluator =
                new DefaultWebSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        return evaluator;
    }

    @Bean
    public DefaultSubjectDAO subjectDAO(DefaultWebSessionStorageEvaluator evaluator) {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(evaluator);
        return subjectDAO;
    }

    @Bean("securityManager")
    @ConditionalOnProperty(name = "onex.shiro.type", havingValue = "jwt", matchIfMissing = true)
    public SecurityManager securityManager(ShiroJwtRealm shiroJwtRealm, SessionManager sessionManager,  DefaultSubjectDAO subjectDAO) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroJwtRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(null);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    @Bean("securityManager")
    @ConditionalOnProperty(name = "onex.shiro.type", havingValue = "uuid", matchIfMissing = false)
    public SecurityManager securityUuidManager(ShiroUuidRealm shiroUuidRealm, SessionManager sessionManager,  DefaultSubjectDAO subjectDAO) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroUuidRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(null);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

}

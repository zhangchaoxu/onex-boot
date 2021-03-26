package com.nb6868.onexboot.api.common.config;

import com.nb6868.onexboot.api.modules.uc.shiro.ShiroFilter;
import com.nb6868.onexboot.api.modules.uc.shiro.ShiroRealm;
import com.nb6868.onexboot.api.modules.uc.shiro.SimpleShiroFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
public class ShiroConfig {

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean("securityManager")
    public SecurityManager securityManager(ShiroRealm shiroRealm, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        // shiro过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("shiro", new ShiroFilter());
        filters.put("simpleShiro", new SimpleShiroFilter());
        shiroFilter.setFilters(filters);
        /*
         * 自定义url规则 {http://shiro.apache.org/web.html#urls-}
         * *注意*
         * 1. 无法区分接口请求方法是post/get/put
         * 2. anon接口不过shiro,无法记录访问用户信息
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        // 匿名访问地址
        filterMap.put("/static/**", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        // swagger
        filterMap.put("/swagger-ui/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/v3/api-docs", "anon");
        //
        filterMap.put("/service/**", "anon");
        filterMap.put("/editor-app/**", "anon");
        filterMap.put("/diagram-viewer/**", "anon");
        filterMap.put("/modeler.html", "anon");
        filterMap.put("/favicon.ico", "anon");
        // 图形验证码
        filterMap.put("/sys/captcha/base64", "anon");
        filterMap.put("/sys/captcha/stream", "anon");
        // 登录注册
        filterMap.put("/uc/user/loginEncrypt", "anon");
        filterMap.put("/uc/user/login", "anon");
        filterMap.put("/uc/user/register", "anon");
        filterMap.put("/uc/userOauth/wxMaLoginByCodeAndUserInfo", "anon");
        filterMap.put("/uc/userOauth/wxMaLoginByCode", "anon");
        filterMap.put("/uc/userOauth/wxMaLoginByPhone", "anon");
        filterMap.put("/uc/userOauth/dingtalkLoginByCode", "anon");
        // 发送消息
        filterMap.put("/msg/mailLog/sendCode", "anon");
        // cms开放接口
        filterMap.put("/cms/public/*", "anon");
        // simple shiro
        filterMap.put("/sys/param/getLoginAdmin", "simpleShiro");

        // 除上述anon外,其它都需要过oauth2
        filterMap.put("/**", "shiro");
        // 加入注解中含有anon的
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}

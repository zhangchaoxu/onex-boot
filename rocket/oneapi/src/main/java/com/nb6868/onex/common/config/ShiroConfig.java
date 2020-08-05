package com.nb6868.onex.common.config;

import com.nb6868.onex.modules.uc.shiro.ShiroFilter;
import com.nb6868.onex.modules.uc.shiro.ShiroRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 *
 * @author Charles (zhanngchaoxu@gmail.com)
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
        shiroFilter.setFilters(filters);
        /*
         * 自定义url规则 {http://shiro.apache.org/web.html#urls-}
         * 注意无法区分接口请求方法是post/get/put
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/static/**", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/service/**", "anon");
        filterMap.put("/editor-app/**", "anon");
        filterMap.put("/diagram-viewer/**", "anon");
        filterMap.put("/modeler.html", "anon");
        filterMap.put("/captcha", "anon");
        filterMap.put("/favicon.ico", "anon");
        // 除上述anon外,其它都需要过oauth2
        filterMap.put("/**", "shiro");
        // 加入注解中含有anon的
        filterMap.putAll(getAnonAccessSet());
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Autowired
    Environment env;

    private Map<String, String> getAnonAccessSet() {
        Map<String, String> filterRuleMap = new LinkedHashMap<>();

        // 获得所有controller
        /*String[] controllerNameList = SpringContextUtils.getBeanNamesForAnnotation(org.springframework.stereotype.Controller.class);
        String[] restControllerNameList = SpringContextUtils.getBeanNamesForAnnotation(org.springframework.web.bind.annotation.RestController.class);

        for (String controllerName : restControllerNameList) {
            Class<?> clazz = SpringContextUtils.getType(controllerName);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                AnonAccess annotation = method.getAnnotation(AnonAccess.class);
                if (null != annotation) {
                    filterRuleMap.put(annotation.path(), "anon");
                }
            }

        }*/

        return filterRuleMap;
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

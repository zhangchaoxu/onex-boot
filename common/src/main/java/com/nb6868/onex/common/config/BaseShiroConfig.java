package com.nb6868.onex.common.config;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.filter.JwtShiroFilter;
import com.nb6868.onex.common.filter.SimpleShiroFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.Filter;
import java.util.*;

/**
 * Shiro配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class BaseShiroConfig {

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // shiro session默认30min,
        // web中处理异步长耗时任务若超30min,会报org.apache.shiro.session.ExpiredSessionException
        sessionManager.setGlobalSessionTimeout(-1);
        sessionManager.setSessionValidationSchedulerEnabled(false);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager, AuthProps authProps) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // shiro过滤
        shiroFilter.setFilters(initFilters(authProps));
        // 加入注解中含有anon的
        shiroFilter.setFilterChainDefinitionMap(initFilterMap(authProps));
        return shiroFilter;
    }

    /**
     * 初始化filters
     */
    protected Map<String, Filter> initFilters(AuthProps authProps) {
        Map<String, Filter> filters = new HashMap<>();
        filters.put("shiro", new SimpleShiroFilter(authProps));
        filters.put("jwtShiro", new JwtShiroFilter(authProps));
        return filters;
    }

    /**
     * 初始化过滤map
     */
    protected Map<String, String> initFilterMap(AuthProps authProps) {
        /*
         * 自定义url规则 {http://shiro.apache.org/web.html#urls-}
         * *注意*
         * 1. 无法区分接口请求方法是post/get/put
         * 2. anon接口不过shiro,无法记录访问用户信息
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        // 允许匿名访问地址
        // favicon
        filterMap.put("/favicon.ico", "anon");
        // websocket
        filterMap.put("/ws/**", "anon");
        // 静态文件
        filterMap.put("/static/**", "anon");
        filterMap.put("/webjars/**", "anon");
        // druid
        filterMap.put("/druid/**", "anon");
        // swagger
        filterMap.put("/doc.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        // activities
        filterMap.put("/service/**", "anon");
        filterMap.put("/editor-app/**", "anon");
        filterMap.put("/diagram-viewer/**", "anon");
        filterMap.put("/modeler.html", "anon");
        // easypoi
        // filterMap.put("/easypoi-preview.html", "anon");
        // filterMap.put("/easypoijs/**", "anon");
        // filterMap.put("/easypoi/wps/**", "anon");
        // 扫描RequestMapping类
        StrUtil.splitTrim(authProps.getAccessScanPackage(), ',').forEach(scanPackage -> ClassUtil.scanPackageByAnnotation(scanPackage, RequestMapping.class).forEach(cls -> {
            // 方法中获取注解
            RequestMapping requestMappingAnnotation = cls.getAnnotation(RequestMapping.class);
            if (null != requestMappingAnnotation) {
                // 先判断是否有类注解
                AccessControl accessControlClassAnnotation = cls.getAnnotation(AccessControl.class);
                if (null != accessControlClassAnnotation) {
                    // 有类注解
                    if (accessControlClassAnnotation.value().length == 0) {
                        // 类注解AccessControl未声明value,用RequestMapping中的值
                        for (String value : requestMappingAnnotation.value()) {
                            filterMap.put(value + (value.endsWith("/") ? "**" : "/**"), accessControlClassAnnotation.filter());
                        }
                    } else {
                        // 类注解AccessControl声明value
                        for (String value : accessControlClassAnnotation.value()) {
                            filterMap.put(value, accessControlClassAnnotation.filter());
                        }
                    }
                }
                // 再处理方法注解
                ClassUtil.getPublicMethods(cls, method -> null != method.getAnnotation(AccessControl.class)).forEach(method -> {
                    AccessControl accessControlMethodAnnotation = method.getAnnotation(AccessControl.class);
                    if (accessControlMethodAnnotation != null) {
                        if (accessControlMethodAnnotation.value().length == 0) {
                            // 方法注解AccessControl未声明value,用方法中RequestMapping中的值
                            RequestMapping requestMappingMethodAnnotation = method.getAnnotation(RequestMapping.class);
                            if (requestMappingMethodAnnotation != null) {
                                for (String value : requestMappingMethodAnnotation.value()) {
                                    for (String requestMappingValue : requestMappingAnnotation.value()) {
                                        filterMap.put(requestMappingValue + value, accessControlMethodAnnotation.filter());
                                    }
                                }
                            }
                        } else {
                            // 方法注解AccessControl声明value
                            for (String value : accessControlMethodAnnotation.value()) {
                                for (String requestMappingValue : requestMappingAnnotation.value()) {
                                    filterMap.put(requestMappingValue + value, accessControlMethodAnnotation.filter());
                                }
                            }
                        }
                    }
                });
            }
        }));
        // 除上述anon外,其它都需要过shiro
        filterMap.put("/**", "jwtShiro");
        filterMap.forEach((s, s2) -> log.debug("shiro key={}, filter={}", s, s2));
        return filterMap;
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

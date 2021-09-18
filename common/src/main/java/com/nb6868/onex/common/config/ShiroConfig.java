package com.nb6868.onex.common.config;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ClassUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.filter.JwtShiroFilter;
import com.nb6868.onex.common.filter.ShiroFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.Filter;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Shiro配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class ShiroConfig {

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
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
        filters.put("shiro", new ShiroFilter(authProps));
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
        // 扫描RequestMapping类
        Set<Class<?>> requestMapClassSet = new HashSet<>();
        StrSplitter.splitTrim(authProps.getAccessScanPackage(), ',', true).forEach(s -> requestMapClassSet.addAll(ClassUtil.scanPackageByAnnotation(s, RequestMapping.class)));
        requestMapClassSet.forEach(cls -> {
            // 方法中获取注解
            RequestMapping requestMappingAnnotation = cls.getAnnotation(RequestMapping.class);
            if (null != requestMappingAnnotation) {
                // 先判断是否有类注解
                AccessControl accessControlClassAnnotation = cls.getAnnotation(AccessControl.class);
                if (null == accessControlClassAnnotation) {
                    // 没有类注解,判断方法注解
                    Method[] methods = cls.getDeclaredMethods();
                    if (!ObjectUtils.isEmpty(methods)) {
                        for (Method method : methods) {
                            AccessControl accessControlMethodAnnotation =  method.getAnnotation(AccessControl.class);
                            if (accessControlMethodAnnotation != null) {
                                for (String value : accessControlMethodAnnotation.value()) {
                                    for (String requestMappingValue : requestMappingAnnotation.value()) {
                                        filterMap.put(requestMappingValue + value, accessControlMethodAnnotation.filter());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 有类注解,不再判断方法注解
                    for (String value : accessControlClassAnnotation.value()) {
                        filterMap.put(value, accessControlClassAnnotation.filter());
                    }
                }
            }
        });
        // 除上述anon外,其它都需要过jwt shiro
        filterMap.put("/**", "jwtShiro");
        // filterMap.forEach((s, s2) -> log.debug("shiro key={}, filter={}", s, s2));
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

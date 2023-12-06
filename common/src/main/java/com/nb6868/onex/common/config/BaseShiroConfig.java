package com.nb6868.onex.common.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.filter.SimpleShiroFilter;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.InvalidRequestFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @Bean
    public ShiroFilterFactoryBean shirFilter(@Qualifier("securityManager") SecurityManager securityManager, AuthProps authProps) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        // 设置securityManager
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
        // 关闭InvalidRequestFilter,避免对中文文件的读取错误
        // see https://blog.csdn.net/ngl272/article/details/111691083
        InvalidRequestFilter invalidRequestFilter = new InvalidRequestFilter();
        // invalidRequestFilter.setBlockSemicolon(false);
        // invalidRequestFilter.setBlockBackslash(false);
        // 关闭校验
        invalidRequestFilter.setBlockNonAscii(false);
        filters.put(DefaultFilter.invalidRequest.name(), invalidRequestFilter);
        filters.put("shiro", new SimpleShiroFilter(authProps.getTokenHeaderKey()));
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
        // 允许匿名访问地址,在authProp中添加白名单
        StrUtil.splitTrim(authProps.getWhiteList(), ",").forEach(s -> filterMap.put(s, "anon"));
        // 允许匿名访问地址,在Controller中用AccessControl加入
        // 扫描RequestMapping类
        if (authProps.getAccessControl() != null && authProps.getAccessControl().isEnable() && StrUtil.isNotBlank(authProps.getAccessControl().getScanPackage())) {
            StrUtil.splitTrim(authProps.getAccessControl().getScanPackage(), ',').forEach(scanPackage ->
                    // 扫描类上的RequestMapping注解，找到所有Controller
                    ClassUtil.scanPackageByAnnotation(scanPackage, RequestMapping.class)
                            .forEach(cls -> {
                                RequestMapping requestMappingAnnotation = cls.getAnnotation(RequestMapping.class);
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
                                // 再处理public方法注解
                                // 当部分类找不到会出问题
                                // 比如在AuthController.sendMsgCode, 因为调用了msg包中内容
                                // 但实际项目不需要msg,就会NoClassDefFoundError(Throwable可捕捉)
                                // 导致整个AuthController中其它方法也无法AccessControl
                                CollUtil.emptyIfNull(ClassUtil.getPublicMethods(cls, method -> null != method.getAnnotation(AccessControl.class))).forEach(method -> {
                                    // 找到类中带有AccessControl的注解
                                    AccessControl accessControlMethodAnnotation = method.getAnnotation(AccessControl.class);
                                    if (accessControlMethodAnnotation.value().length == 0) {
                                        // 方法注解AccessControl未声明value,用方法中RequestMapping中的值
                                        String[] mappingValue = null;
                                        PostMapping postMappingMethodAnnotation = method.getAnnotation(PostMapping.class);
                                        if (null != postMappingMethodAnnotation) {
                                            mappingValue = postMappingMethodAnnotation.value();
                                        } else {
                                            GetMapping getMappingMethodAnnotation = method.getAnnotation(GetMapping.class);
                                            if (null != getMappingMethodAnnotation) {
                                                mappingValue = getMappingMethodAnnotation.value();
                                            } else {
                                                PutMapping putMappingMethodAnnotation = method.getAnnotation(PutMapping.class);
                                                if (null != putMappingMethodAnnotation) {
                                                    mappingValue = putMappingMethodAnnotation.value();
                                                } else {
                                                    DeleteMapping deleteMappingMethodAnnotation = method.getAnnotation(DeleteMapping.class);
                                                    if (null != deleteMappingMethodAnnotation) {
                                                        mappingValue = deleteMappingMethodAnnotation.value();
                                                    } else {
                                                        RequestMapping requestMappingMethodAnnotation = method.getAnnotation(RequestMapping.class);
                                                        if (null != requestMappingMethodAnnotation) {
                                                            mappingValue = requestMappingMethodAnnotation.value();
                                                        } else {
                                                            // 找不到哦
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (ObjectUtil.isNotEmpty(mappingValue)) {
                                            for (String value : mappingValue) {
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
                                });
                            }));
        }
        // 除上述anon外,其它都需要过shiro
        filterMap.put("/**", "shiro");
        filterMap.forEach((s, s2) -> log.debug("shiro key={}, filter={}", s, s2));
        return filterMap;
    }

    /**
     * 解决@RequiresAuthentication注解不生效的配置
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 为Spring-Bean开启对Shiro注解的支持
     * 启用shrio授权注解拦截方式，AOP式方法级权限检查
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}

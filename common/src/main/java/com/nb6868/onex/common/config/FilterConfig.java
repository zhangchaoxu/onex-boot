package com.nb6868.onex.common.config;

import com.nb6868.onex.common.filter.CrosFilter;
import com.nb6868.onex.common.filter.HttpServletRequestReplaceFilter;
import com.nb6868.onex.common.filter.XssFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Filter配置
 * 过滤器的顺序按照order来
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.filter.enable", havingValue = "true")
public class FilterConfig {

    // filter的初始化在bean之前，无法Autowired
    // 需要在这里用Bean初始化
    @Bean
    @ConditionalOnProperty(name = "onex.filter.cros", havingValue = "true")
    public Filter crosFilter() {
        return new CrosFilter();
    }

    @Bean
    @ConditionalOnProperty(name = "onex.filter.http-request-replace", havingValue = "true")
    public FilterRegistrationBean<?> httpRequestReplaceFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new HttpServletRequestReplaceFilter());
        registration.addUrlPatterns("/*");
        //registration.setName("httpRequestReplaceFilter");
        registration.setOrder(Integer.MAX_VALUE - 100);
        return registration;
    }

    @Bean
    @ConditionalOnProperty(name = "onex.filter.cros", havingValue = "true")
    public FilterRegistrationBean<?> crosFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(crosFilter());
        registration.addUrlPatterns("/*");
        //registration.setName("crosFilter");
        registration.setOrder(Integer.MAX_VALUE - 90);
        return registration;
    }

    /**
     * shiroFilter的bean注册在BaseShiroConfig
     */
    @Bean
    @ConditionalOnProperty(name = "onex.filter.shiro", havingValue = "true")
    public FilterRegistrationBean<?> shiroFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        // 这里代理的shiroFilter指的是ShiroConfig中的shirFilter,而不是shirFilter类本身
        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        // 该值缺省为false，表示生命周期由SpringApplicationContext管理，设置为true则表示由ServletContainer管理
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setEnabled(true);
        registration.addUrlPatterns("/*");
        // 这里如果shiroFilter会导致和ShiroConfig中的shiroFilter冲突
        //registration.setName("shiroFilterRegistration");
        registration.setOrder(Integer.MAX_VALUE - 80);
        return registration;
    }

    @Bean
    @ConditionalOnProperty(name = "onex.filter.xss", havingValue = "true")
    public FilterRegistrationBean<?> xssFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        //registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }

}

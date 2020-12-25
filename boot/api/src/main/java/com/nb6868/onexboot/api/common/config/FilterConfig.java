package com.nb6868.onexboot.api.common.config;

import com.nb6868.onexboot.common.xss.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

/**
 * Filter配置
 * 过滤器的顺序按照order来，cros->shiro->xss
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<?> crosFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new CrosFilter());
        registration.addUrlPatterns("/*");
        registration.setName("crosFilter");
        registration.setOrder(Integer.MAX_VALUE - 2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<?> shiroFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        // 该值缺省为false，表示生命周期由SpringApplicationContext管理，设置为true则表示由ServletContainer管理
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setEnabled(true);
        registration.setOrder(Integer.MAX_VALUE - 1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<?> xssFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}

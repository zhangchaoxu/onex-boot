package com.nb6868.onex.common.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * cros filter
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class CrosFilter implements Filter {

    /**
     * filter的初始化在bean之前
     * 所以在init的时候是null
     * 需要在filterConfig中用@Bean注入
     */
    @Autowired
    CrosProps crosProps;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * cros处理，避免前端跨域问题
     * 注意所有前端请求header都需要注册
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, crosProps.getAllowCredentials());
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,crosProps.getExposeHeaders());
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, crosProps.getAllowHeaders());
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, crosProps.getAllowMethods());
        // Access-Control-Allow-Origin和Access-Control-Allow-Credentials有约束;
        // Credentials true,Origin必须指定具体来源,不能用*通配;
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, crosProps.getMaxAge());
        //  直接放行Options,提高接口访问速度
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}

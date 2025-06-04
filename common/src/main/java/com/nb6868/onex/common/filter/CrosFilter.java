package com.nb6868.onex.common.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * cros filter
 * 跨域是漏扫很容易出问题的地方
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
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,crosProps.getExposeHeaders());
        // 检查请求头
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, crosProps.getAllowHeaders());
        // 检查请求方法
        if (!StrUtil.containsIgnoreCase(crosProps.getAllowMethods(), request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        // Access-Control-Allow-Origin和Access-Control-Allow-Credentials有约束;
        // Credentials true,Origin必须指定具体来源,不能用*通配;
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, crosProps.getAllowCredentials());
        // 检查请求来源
        String headerOrigin = request.getHeader(HttpHeaders.ORIGIN);
        // origin为空，或者不在范围内，则forbidden
        if (StrUtil.isBlank(headerOrigin) || (StrUtil.isNotBlank(crosProps.getAllowOrigin()) && StrUtil.containsIgnoreCase(crosProps.getAllowOrigin(), headerOrigin))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headerOrigin);
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

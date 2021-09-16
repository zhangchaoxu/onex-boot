package com.nb6868.onex.common.filter;

import com.nb6868.onex.common.auth.LoginProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt token filter
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class JwtFilter implements Filter {

    /**
     * filter的初始化在bean之前
     * 所以在init的时候是null
     * 需要在filterConfig中用@Bean注入
     */
    @Autowired
    LoginProps loginProps;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 请求token
        String token = HttpContextUtils.getRequestParameter((HttpServletRequest) servletRequest, loginProps.getTokenKey());
        if (ObjectUtils.isEmpty(token)) {
            // 如果token不存在，直接返回401
            responseUnauthorized(servletRequest, servletResponse, null);
        } else {
            // 验证token
            // 通过
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @SneakyThrows
    @SuppressWarnings("deprecation")
    protected void responseUnauthorized(ServletRequest request, ServletResponse response, Exception e) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ((HttpServletRequest) request).getHeader(HttpHeaders.ORIGIN));

        // 处理登录失败的异常
        if (e == null) {
            String json = JacksonUtils.pojoToJson(new Result<>().error(ErrorCode.UNAUTHORIZED));
            httpResponse.getWriter().print(json);
        } else {
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            String json = JacksonUtils.pojoToJson(new Result<>().error(ErrorCode.UNAUTHORIZED), throwable.getMessage());
            httpResponse.getWriter().print(json);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

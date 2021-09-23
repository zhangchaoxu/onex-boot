package com.nb6868.onex.common.filter;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.JacksonUtils;
import lombok.SneakyThrows;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础shiro过滤器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class BaseShiroFilter extends AuthenticatingFilter {

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            return onLoginFailure(token, null, request, response);
        }
        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 放行所有OPTIONS请求
        return RequestMethod.OPTIONS.name().equals(((HttpServletRequest) request).getMethod());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    /**
     * 登录失败
     *
     * Realm.doGetAuthenticationInfo抛出的异常会在这里捕获处理
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        responseUnauthorized(request, response, e);
        return false;
    }

    /**
     * 响应未授权
     */
    @SneakyThrows
    @SuppressWarnings("deprecation")
    protected void responseUnauthorized(ServletRequest request, ServletResponse response, Exception e) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ((HttpServletRequest) request).getHeader(HttpHeaders.ORIGIN));

        // 处理登录失败的异常
        Result<?> result = new Result<>().error(ErrorCode.UNAUTHORIZED);
        if (e != null && e.getCause() != null && StrUtil.isNotBlank(e.getCause().getMessage())) {
            result.setMsg(e.getCause().getMessage());
        }
        httpResponse.getWriter().print(JacksonUtils.pojoToJson(result));
    }

}

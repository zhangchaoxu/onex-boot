package com.nb6868.onexboot.api.modules.uc.shiro;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.HttpContextUtils;
import com.nb6868.onexboot.common.util.JacksonUtils;
import lombok.SneakyThrows;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shiro过滤器
 * 代码的执行流程preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ShiroFilter extends AuthenticatingFilter {

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        // 请求token
        final String token = HttpContextUtils.getRequestParameter((HttpServletRequest) request, UcConst.TOKEN_HEADER);
        // 当请求token为空的时候,赋予匿名访问Token
        return new AuthenticationToken() {
            @Override
            public String getPrincipal() {
                return ObjectUtils.isEmpty(token) ? UcConst.TOKEN_ANON : token;
            }

            @Override
            public String getCredentials() {
                return ObjectUtils.isEmpty(token) ? UcConst.TOKEN_ANON : token;
            }
        };
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 放行所有OPTIONS请求
        return RequestMethod.OPTIONS.name().equals(((HttpServletRequest) request).getMethod());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 会调用createToken,提交给realm进行登入
        return executeLogin(request, response);
    }

    /**
     * 登录失败
     *
     * Realm.doGetAuthenticationInfo抛出的异常会在这里捕获处理
     */
    @SneakyThrows
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader(HttpHeaders.ORIGIN));
        // 处理登录失败的异常
        Throwable throwable = e.getCause() == null ? e : e.getCause();
        String json = JacksonUtils.pojoToJson(new Result<>().error(ErrorCode.UNAUTHORIZED, throwable.getMessage()));
        httpResponse.getWriter().print(json);
        return false;
    }

}

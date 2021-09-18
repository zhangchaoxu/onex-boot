package com.nb6868.onex.common.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.JwtUtils;
import lombok.SneakyThrows;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT Shiro过滤器
 * 代码的执行流程preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class JwtShiroFilter extends AuthenticatingFilter {

    private final AuthProps authProps;

    public JwtShiroFilter(AuthProps authProps) {
        this.authProps = authProps;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        // 请求token
        String token = HttpContextUtils.getRequestParameter((HttpServletRequest) request, authProps.getTokenKey());
        if (StrUtil.isNotBlank(token)) {
            // 验证token
            JWT jwt = JwtUtils.parseToken(token);
            if (null != jwt && StrUtil.isNotBlank(jwt.getPayload().getClaimsJson().getStr("type"))) {
                // 用密码校验
                AuthProps.Config loginConfig = authProps.getConfigs().get(jwt.getPayload().getClaimsJson().getStr("type"));
                // 只验证了密码,没验证有效期
                if (null != loginConfig && JwtUtils.verifyKey(token, loginConfig.getTokenKey())) {
                    // 提交给realm进行登入
                    return new AuthenticationToken() {
                        @Override
                        public JSONObject getPrincipal() {
                            // 身份
                            return jwt.getPayload().getClaimsJson();
                        }

                        @Override
                        public String getCredentials() {
                            // 证明
                            return token;
                        }
                    };
                }
            }
        }
        return null;
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
        if (e == null) {
            String json = JacksonUtils.pojoToJson(new Result<>().error(ErrorCode.UNAUTHORIZED));
            httpResponse.getWriter().print(json);
        } else {
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            String json = JacksonUtils.pojoToJson(new Result<>().error(ErrorCode.UNAUTHORIZED), throwable.getMessage());
            httpResponse.getWriter().print(json);
        }
    }

}

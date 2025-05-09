package com.nb6868.onex.common.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.JacksonUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 基础shiro过滤器
 * 更多shiro用法见<a href="https://shiro.apache.org/tutorial.html">Apache Shiro Tutorial</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public abstract class BaseShiroFilter extends AuthenticatingFilter {

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            return onLoginFailure(token, new AuthenticationException(Const.MSG_LOGIN_REQUIRED), request, response);
        }
        try {
            Subject subject = getSubject(request, response);
            // 尝试登录,login方法最终交由Realm中doGetAuthenticationInfo进行认证
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException ae) {
            // 在doGetAuthenticationInfo->doGetAuthorizationInfo过长中出现的异常，会在这里捕捉到
            // 比如主动抛出的new AuthenticationException("登录信息已失效,请重新登录...")
            // 比如数据库查询shiroDao.getUserTokenByToken(token)过程出现的其它异常信息
            // 妈了个蛋，在AbstractAuthenticator对所有异常都封装成了AuthenticationException
            // 因此需要对AuthenticationException做解构判断(放到responseUnauthorized处理)
            // log.error("shiro login exception", ae);
            return onLoginFailure(token, ae, request, response);
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
     * Realm.doGetAuthenticationInfo抛出的异常会在这里捕获处理
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        responseUnauthorized(request, response, e);
        return false;
    }

    /**
     * 响应未授权,处理此Filter中的禁止通行的情况
     */
    @SneakyThrows
    @SuppressWarnings("deprecation")
    protected void responseUnauthorized(ServletRequest request, ServletResponse response, AuthenticationException e) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ((HttpServletRequest) request).getHeader(HttpHeaders.ORIGIN));
        // 处理登录失败的异常
        // 正常的AuthenticationException不带有cause
        Result<?> result;
        if (ObjUtil.isNotNull(e) && ObjUtil.isNotNull(e.getCause())) {
            // 带有cause的实际是内部异常,而不是授权失败
            result = new Result<>().error(ErrorCode.INTERNAL_SERVER_ERROR);
            String errorMsg = ExceptionUtil.getSimpleMessage(e.getCause());
            if (StrUtil.isNotBlank(errorMsg)) {
                result.setMsg(errorMsg);
            }
            log.error("shiro login exception", e.getCause());
        } else {
            // 不带有cause的才是真正的授权异常
            result = new Result<>().error(ErrorCode.UNAUTHORIZED);
            if (ObjUtil.isNotNull(e)) {
                String errorMsg = ExceptionUtil.getSimpleMessage(e);
                if (StrUtil.isNotBlank(errorMsg)) {
                    result.setMsg(errorMsg);
                }
            }
            log.error("shiro login reject: {}", result.getMsg());
        }

        httpResponse.getWriter().print(JacksonUtils.pojoToJson(result));
    }

}

package com.nb6868.onexboot.api.modules.uc.shiro;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.common.util.HttpContextUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * SimpleShiro过滤器,会虚拟出一个ANON用户来访问
 * 代码的执行流程preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SimpleShiroFilter extends ShiroFilter {

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
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

}

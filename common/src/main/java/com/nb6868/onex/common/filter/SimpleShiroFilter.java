package com.nb6868.onex.common.filter;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.util.HttpContextUtils;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Shiro过滤器
 * 代码的执行流程preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SimpleShiroFilter extends BaseShiroFilter {

    /**
     * token在header中的key，定义在配置文件中
     */
    private final String tokenHeaderKey;

    public SimpleShiroFilter(String tokenHeaderKey) {
        this.tokenHeaderKey = tokenHeaderKey;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        // 请求token
        String token = HttpContextUtils.getRequestParameter((HttpServletRequest) request, tokenHeaderKey);
        final String finalToken = StrUtil.removePrefix(StrUtil.removePrefix(token, "Bearer "), "bearer ");
        if (StrUtil.isNotBlank(token)) {
            return new AuthenticationToken() {
                @Override
                public String getPrincipal() {
                    // 身份
                    return finalToken;
                }

                @Override
                public String getCredentials() {
                    // 证明
                    return finalToken;
                }
            };
        }
        return null;
    }

}

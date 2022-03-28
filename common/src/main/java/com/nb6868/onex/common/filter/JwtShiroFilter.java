package com.nb6868.onex.common.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JwtUtils;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * JWT Shiro过滤器
 * 代码的执行流程preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class JwtShiroFilter extends BaseShiroFilter {

    private final AuthProps authProps;

    public JwtShiroFilter(AuthProps authProps) {
        this.authProps = authProps;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String token = HttpContextUtils.getRequestParameter((HttpServletRequest) request, authProps.getTokenKey());
        // 验证token
        JWT jwt = JwtUtils.parseToken(token);
        if (null != jwt) {
            String tokenType = jwt.getPayload().getClaimsJson().getStr(authProps.getTokenTypeKey());
            if (StrUtil.isNotBlank(tokenType)) {
                // 获得改类型的配置
                AuthProps.Config loginConfig = authProps.getConfigs().get(tokenType);
                // 只验证了密码,没验证有效期
                if (null != loginConfig && JwtUtils.verifyKey(token, loginConfig.getTokenKey().getBytes())) {
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

}

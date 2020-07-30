package com.nb6868.onex.common.interceptor;

import com.nb6868.onex.booster.util.HttpContextUtils;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.modules.uc.UcConst;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限访问权限验证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class AuthAccessInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = HttpContextUtils.getRequestParameter(request, UcConst.TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            // 匿名访问
            AnonAccess annotation;
            if (handler instanceof HandlerMethod) {
                annotation = ((HandlerMethod) handler).getMethodAnnotation(AnonAccess.class);
            } else {
                return true;
            }

            /*if (annotation == null || !annotation.value()) {
                throw new OnexException(ErrorCode.UNAUTHORIZED);
            }*/
        }
        return true;
    }
}

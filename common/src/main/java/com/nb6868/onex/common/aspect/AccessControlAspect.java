package com.nb6868.onex.common.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.HttpContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 访问控制，切面处理类
 * 注意,这里只做ip的控制,filter的控制由shiro实现
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
@ConditionalOnProperty(name = "onex.auth.access-control.enable", havingValue = "true", matchIfMissing = true)
@Order(100)
@Slf4j
public class AccessControlAspect {

    @Autowired
    private Environment env;

    @Pointcut("@annotation(com.nb6868.onex.common.annotation.AccessControl)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        AccessControl annotation = method.getAnnotation(AccessControl.class);
        if (annotation != null) {
            if ("white".equalsIgnoreCase(annotation.ipFilter()) && ObjectUtil.isNotEmpty(annotation.ipWhite())) {
                // 白名单
                String ip = HttpContextUtils.getIpAddr(HttpContextUtils.getHttpServletRequest());
                for (String s : annotation.ipWhite()) {
                    if (ReUtil.isMatch(s, ip)) {
                        return checkAccessToken(annotation) ? joinPoint.proceed() : new Result<>().error(ErrorCode.ACCESS_TOKEN_FORBID);
                    }
                }
                // 返回错误
                return new Result<>().error(ErrorCode.IP_BLACK);
            } else if ("black".equalsIgnoreCase(annotation.ipFilter()) && ObjectUtil.isNotEmpty(annotation.ipBlack())) {
                // 黑名单
                String ip = HttpContextUtils.getIpAddr(HttpContextUtils.getHttpServletRequest());
                for (String s : annotation.ipBlack()) {
                    if (ReUtil.isMatch(s, ip)) {
                        return new Result<>().error(ErrorCode.IP_BLACK);
                    }
                }
                return checkAccessToken(annotation) ? joinPoint.proceed() : new Result<>().error(ErrorCode.ACCESS_TOKEN_FORBID);
            } else {
                // 不检查ip
                return checkAccessToken(annotation) ? joinPoint.proceed() : new Result<>().error(ErrorCode.ACCESS_TOKEN_FORBID);
            }
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     * 检查access token
     */
    private boolean checkAccessToken(AccessControl annotation) {
        if (ObjectUtil.isNotEmpty(annotation.allowTokenName())) {
            for (String allowTokenName : annotation.allowTokenName()) {
                String token = HttpContextUtils.getRequestParameter(allowTokenName);
                if (StrUtil.isNotBlank(token)) {
                    if (token.equalsIgnoreCase(env.getProperty("onex.auth.access-control." + allowTokenName))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

}

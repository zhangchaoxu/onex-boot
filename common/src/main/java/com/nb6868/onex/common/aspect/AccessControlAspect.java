package com.nb6868.onex.common.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
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
import org.springframework.core.annotation.Order;
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
@Slf4j
@Order(100)
public class AccessControlAspect {

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
                        return joinPoint.proceed();
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
                return joinPoint.proceed();
            } else {
                // 放行
                return joinPoint.proceed();
            }
        } else {
            return joinPoint.proceed();
        }
    }

}

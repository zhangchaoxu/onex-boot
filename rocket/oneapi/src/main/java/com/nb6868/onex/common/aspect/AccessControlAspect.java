package com.nb6868.onex.common.aspect;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.user.SecurityUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 匿名访问，切面处理类
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Aspect
@Component
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
            // 注解上的控制
            if (!annotation.anon()) {
                if (UcConst.TOKEN_ANON.equalsIgnoreCase(SecurityUser.getUser().getToken())) {
                    throw new OnexException(ErrorCode.UNAUTHORIZED);
                }
            }
        }

        return joinPoint.proceed();
    }

}

package com.nb6868.onex.api.common.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.nb6868.onex.common.exception.OnexException;
import lombok.extern.log4j.Log4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 限流拦截器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
public class AccessLimitInterceptor {

    // 每秒只发出3个令牌,内部采用令牌捅算法实现
    private static RateLimiter rateLimiter = RateLimiter.create(5.0);

    @Around("execution(public * *(..)) && @annotation(com.nb6868.onex.common.annotation.AccessLimit)")
    public Object interceptor(ProceedingJoinPoint joinPoint) {
        Object obj = null;
        try {
            //tryAcquire()是非阻塞, rateLimiter.acquire()是阻塞的
            if (rateLimiter.tryAcquire()) {
                obj = joinPoint.proceed();
            } else {
                throw new OnexException("服务降级");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }

}

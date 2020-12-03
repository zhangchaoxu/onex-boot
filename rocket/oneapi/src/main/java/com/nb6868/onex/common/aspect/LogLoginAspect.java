package com.nb6868.onex.common.aspect;

import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.util.HttpContextUtils;
import com.nb6868.onex.common.annotation.LogLogin;
import com.nb6868.onex.modules.log.entity.LoginEntity;
import com.nb6868.onex.modules.log.service.LoginService;
import com.nb6868.onex.modules.uc.dto.LoginRequest;
import com.nb6868.onex.modules.uc.user.SecurityUser;
import com.nb6868.onex.modules.uc.user.UserDetail;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 登录日志，切面处理类
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Aspect
@Component
public class LogLoginAspect {

    @Autowired
    private LoginService loginService;

    @Pointcut("@annotation(com.nb6868.onex.common.annotation.LogLogin)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 保存日志
            saveLog(joinPoint, Const.ResultEnum.SUCCESS.value(), "ok");
            return result;
        } catch (Exception e) {
            // 保存日志
            if (e instanceof OnexException) {
                saveLog(joinPoint, Const.ResultEnum.FAIL.value(), e.toString());
            } else {
                saveLog(joinPoint, Const.ResultEnum.FAIL.value(), e.getMessage());
            }
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Integer result, String msg) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LoginEntity log = new LoginEntity();

        // 登录用户信息
        UserDetail user = SecurityUser.getUser();
        log.setCreateName(user.getUsername());
        int type = 0;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof LoginRequest) {
                LoginRequest loginRequest = (LoginRequest) arg;
                type = loginRequest.getType();
                // 从登录请求中获取用户信息
                if (ObjectUtils.isEmpty(user.getUsername())) {
                    if (!ObjectUtils.isEmpty(loginRequest.getUsername())) {
                        log.setCreateName(loginRequest.getUsername());
                    }  if (!ObjectUtils.isEmpty(loginRequest.getMobile())) {
                        log.setCreateName(loginRequest.getMobile());
                    }
                }
                break;
            }
        }
        if (0 == type) {
            try {
                Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
                LogLogin annotation = method.getAnnotation(LogLogin.class);
                if (annotation != null) {
                    // 注解上的类型
                    type = annotation.type().value();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        log.setResult(result);
        log.setMsg(msg);
        log.setType(type);

        // 请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.setIp(HttpContextUtils.getIpAddr(request));
        if (null != request) {
            log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            log.setIp(HttpContextUtils.getIpAddr(request));
        }

        loginService.save(log);
    }

}

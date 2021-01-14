package com.nb6868.onexboot.api.common.aspect;

import com.nb6868.onexboot.api.common.annotation.LogLogin;
import com.nb6868.onexboot.api.modules.log.entity.LoginEntity;
import com.nb6868.onexboot.api.modules.log.service.LoginService;
import com.nb6868.onexboot.api.modules.uc.dto.LoginRequest;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.HttpContextUtils;
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
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
public class LogLoginAspect {

    @Autowired
    private LoginService loginService;

    @Pointcut("@annotation(com.nb6868.onexboot.api.common.annotation.LogLogin)")
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
        LoginEntity log = new LoginEntity();

        // 登录用户信息
        UserDetail user = SecurityUser.getUser();
        log.setCreateName(user.getUsername());
        String type = "";
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
        if (ObjectUtils.isEmpty(type)) {
            try {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
                LogLogin annotation = method.getAnnotation(LogLogin.class);
                if (annotation != null) {
                    // 注解上的类型
                    type = annotation.type();
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

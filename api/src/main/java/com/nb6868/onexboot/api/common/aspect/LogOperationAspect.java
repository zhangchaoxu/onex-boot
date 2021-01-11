package com.nb6868.onexboot.api.common.aspect;

import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.util.HttpContextUtils;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.log.entity.OperationEntity;
import com.nb6868.onexboot.api.modules.log.service.OperationService;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 操作日志，切面处理类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
public class LogOperationAspect {

    @Autowired
    private OperationService logOperationService;

    @Pointcut("@annotation(com.nb6868.onexboot.api.common.annotation.LogOperation)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始执行时间
        long beginTime = System.currentTimeMillis();
        // 需要先把param拿出来,不然processed以后可能会被修改赋值
        String requestParam = getRequestParam(joinPoint);
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 保存日志
            saveLog(joinPoint, requestParam, System.currentTimeMillis() - beginTime, Const.ResultEnum.SUCCESS.value());
            return result;
        } catch (Exception e) {
            //保存日志
            saveLog(joinPoint, requestParam, System.currentTimeMillis() - beginTime, Const.ResultEnum.FAIL.value());
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, String requestParam, long time, Integer status) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationEntity log = new OperationEntity();

        try {
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
            LogOperation annotation = method.getAnnotation(LogOperation.class);
            if (annotation != null) {
                // 注解上的描述
                log.setOperation(annotation.value());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 登录用户信息
        UserDetail user = SecurityUser.getUser();
        log.setCreateName(user.getUsername());
        log.setStatus(status);
        log.setRequestTime(time);

        // 请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.setIp(HttpContextUtils.getIpAddr(request));
        if (null != request) {
            log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            log.setUri(request.getRequestURI());
            log.setMethod(request.getMethod());
        }
        log.setParams(requestParam);
        logOperationService.save(log);
    }

    /**
     * 从joinPoint获取参数
     */
    private String getRequestParam(ProceedingJoinPoint joinPoint) {
        // 请求参数,接口方法中的参数,可能会有HttpServletRequest、HttpServletResponse、ModelMap
        Object[] args = joinPoint.getArgs();
        List<Object> actualParam = new ArrayList<>();
        for (Object arg : args) {
            // 只处理能处理的
            if (arg == null || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof ModelMap) {
                // 不处理的特例
                break;
            } else if (arg instanceof MultipartFile) {
                actualParam.add(Kv.init().set("type", "file").set("name", ((MultipartFile) arg).getOriginalFilename()));
            } else if (arg instanceof MultipartFile[]) {
                MultipartFile[] files = (MultipartFile[]) arg;
                List<Kv> list = new ArrayList<>();
                for (MultipartFile file : files) {
                    list.add(Kv.init().set("type", "file").set("name", file.getOriginalFilename()));
                }
                actualParam.add(list);
            } else if (arg instanceof Serializable || arg instanceof Map) {
                actualParam.add(arg);
            } else {
                actualParam.add(Kv.init().set("type", arg.getClass().getName()));
            }
        }
        if (actualParam.size() == 1) {
            if (actualParam.get(0) instanceof String || actualParam.get(0) instanceof Long || actualParam.get(0) instanceof Integer) {
                return actualParam.get(0).toString();
            } else {
                return JacksonUtils.pojoToJson(actualParam.get(0), null);
            }
        } else if (actualParam.size() > 1) {
            return JacksonUtils.pojoToJson(actualParam, null);
        } else {
            return null;
        }
    }

}

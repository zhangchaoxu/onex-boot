package com.nb6868.onex.common.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.LoginForm;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
@ConditionalOnProperty(name = "onex.aspect.log", havingValue = "true")
@Order(200)
@Slf4j
public class LogOperationAspect {

    @Autowired
    private BaseLogService logService;

    @Pointcut("@annotation(com.nb6868.onex.common.annotation.LogOperation)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始执行时间
        TimeInterval timer = DateUtil.timer();
        // 先把请求参数取出来,否则processed过程可能会对参数值做修改
        String params = getMethodParam(joinPoint);
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 保存日志
            saveLog(joinPoint, params, timer.interval(), Const.ResultEnum.SUCCESS.value(), null);
            return result;
        } catch (Exception e) {
            //保存日志
            saveLog(joinPoint, params, timer.interval(), Const.ResultEnum.FAIL.value(), e);
            throw e;
        }
    }

    /**
     * 保存日志
     */
    private void saveLog(ProceedingJoinPoint joinPoint, String params, long time, Integer state, Exception e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogBody logEntity = new LogBody();

        // 日志记录类型
        String storeType = "db";
        String logType = "operation";
        try {
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
            LogOperation annotation = method.getAnnotation(LogOperation.class);
            if (annotation != null) {
                // 注解上的描述
                logEntity.setOperation(annotation.value());
                logType = annotation.type();
                storeType = annotation.storeType();
            }
        } catch (NoSuchMethodException ne) {
            ne.printStackTrace();
        }
        if ("login".equalsIgnoreCase(logType)) {
            // 登录日志
            try {
                LoginForm loginForm = JSONUtil.toBean(params, LoginForm.class);
                logEntity.setTenantCode(loginForm.getTenantCode());
                if (loginForm.getType().endsWith("USERNAME_PASSWORD")) {
                    logEntity.setCreateName(loginForm.getUsername());
                } else if (loginForm.getType().endsWith("MOBILE_SMS")) {
                    logEntity.setCreateName(loginForm.getMobile());
                }
            } catch (Exception jsonException) {
            }
        }
        logEntity.setStoreType(storeType);
        logEntity.setState(state);
        logEntity.setRequestTime(time);
        logEntity.setType(logType);
        // 保存错误信息
        if (e != null) {
            logEntity.setContent(e instanceof OnexException ? e.toString() : ExceptionUtil.stacktraceToString(e));
        }

        // 请求参数
        Dict requestParams = Dict.create().set("params", params);
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (null != request) {
            logEntity.setUri(request.getRequestURI());
            // 记录内容
            requestParams.set("ip", HttpContextUtils.getIpAddr(request));
            requestParams.set("ua", request.getHeader(HttpHeaders.USER_AGENT));
            requestParams.set("queryString", request.getQueryString());
            requestParams.set("url", request.getRequestURL());
            requestParams.set("method", request.getMethod());
            requestParams.set("contentType", request.getContentType());
            /*if (request instanceof OnexHttpServletRequestWrapper) {
                try {
                    requestParams.set("params", IoUtil.read(request.getInputStream()).toString());
                } catch (IOException ie) {
                    log.error("读取流失败", e);
                }
            }*/
        }
        logEntity.setRequestParams(requestParams);
        logService.saveLog(logEntity);
    }

    /**
     * 从joinPoint获取参数,其实是从方法的参数中获取，经过json解析后，会和实际传参有偏差
     */
    private String getMethodParam(ProceedingJoinPoint joinPoint) {
        // 请求参数,接口方法中的参数,可能会有HttpServletRequest、HttpServletResponse、ModelMap
        Object[] args = joinPoint.getArgs();
        List<Object> actualParam = new ArrayList<>();
        for (Object arg : args) {
            // 只处理能处理的
            if (arg instanceof MultipartFile) {
                actualParam.add(Dict.create().set("type", "file").set("name", ((MultipartFile) arg).getOriginalFilename()));
            } else if (arg instanceof MultipartFile[]) {
                MultipartFile[] files = (MultipartFile[]) arg;
                List<Dict> list = new ArrayList<>();
                for (MultipartFile file : files) {
                    list.add(Dict.create().set("type", "file").set("name", file.getOriginalFilename()));
                }
                actualParam.add(list);
            } else if (arg instanceof Serializable || arg instanceof Map) {
                actualParam.add(arg);
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

package com.nb6868.onex.common.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.log.BaseLogService;
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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
@Slf4j
@Order(200)
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
        // 先把param拿出来,不然processed以后可能会被修改赋值
        String requestParam = getRequestParam(joinPoint);
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 保存日志
            saveLog(joinPoint, requestParam, timer.interval(), Const.ResultEnum.SUCCESS.value(), null);
            return result;
        } catch (Exception e) {
            //保存日志
            saveLog(joinPoint, requestParam, timer.interval(), Const.ResultEnum.FAIL.value(), e);
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, String requestParam, long time, Integer state, Exception e) {
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
        logEntity.setStoreType(storeType);

        // 请求参数
        JSONObject requestParams = new JSONObject();
        // 登录用户信息
        if ("login".equalsIgnoreCase(logType)) {
            // 登录
            try {
                JSONObject loginRequest = JSONUtil.parseObj(requestParam);
                logEntity.setCreateName(MapUtil.getStr(loginRequest, "username"));
                // 移除登录密码,否则会导致密码泄露
                if (loginRequest.containsKey("password")) {
                    loginRequest.set("password", "");
                }
                requestParams.set("params", JSONUtil.toJsonStr(loginRequest));
            } catch (Exception e2) {
                requestParams.set("params", requestParam);
            }
        } else {
            requestParams.set("params", requestParam);
        }
        logEntity.setState(state);
        logEntity.setRequestTime(time);
        logEntity.setType(logType);
        // 保存错误信息
        if (e != null) {
            logEntity.setContent(ExceptionUtil.stacktraceToString(e));
        }

        // 请求相关信息
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (null != request) {
            logEntity.setUri(request.getRequestURI());
            requestParams.set("ip", HttpContextUtils.getIpAddr(request));
            requestParams.set("ua", request.getHeader(HttpHeaders.USER_AGENT));
            requestParams.set("url", request.getRequestURL());
            requestParams.set("method", request.getMethod());
            requestParams.set("contentType", request.getContentType());
        }
        logEntity.setRequestParams(requestParams);
        logService.saveLog(logEntity);
    }

    /**
     * 从joinPoint获取参数
     */
    private String getRequestParam(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // GET请求
        if (request != null && HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            return request.getQueryString();
        }
        // 请求参数,接口方法中的参数,可能会有HttpServletRequest、HttpServletResponse、ModelMap
        Object[] args = joinPoint.getArgs();
        List<Object> actualParam = new ArrayList<>();
        for (Object arg : args) {
            // 只处理能处理的
            if (arg == null || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof ModelMap) {
                // 不处理的特例
                break;
            } else if (arg instanceof MultipartFile) {
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
            } else {
                actualParam.add(Dict.create().set("type", arg.getClass().getName()));
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

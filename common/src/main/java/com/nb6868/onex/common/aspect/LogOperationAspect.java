package com.nb6868.onex.common.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.IpRegionUtil;
import com.nb6868.onex.common.util.JacksonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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
@ConditionalOnProperty(name = "onex.log.enable", havingValue = "true")
@Order(200)
@Slf4j
public class LogOperationAspect {

    @Autowired
    BaseLogService logService;
    // 环境变量，是否将ip转换为区域
    @Value("${onex.log.ip2region.enable:false}")
    private boolean logIp2Region;

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
            saveLog(joinPoint, params, timer.interval(), ErrorCode.SUCCESS, null);
            return result;
        } catch (Exception e) {
            //保存日志
            int state = e instanceof OnexException ? ((OnexException) e).getCode() : ErrorCode.INTERNAL_SERVER_ERROR;
            saveLog(joinPoint, params, timer.interval(), state, e);
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
        String logStoreType = "db";
        String logType = "operation";
        try {
            Method method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
            LogOperation annotation = method.getAnnotation(LogOperation.class);
            if (annotation != null) {
                // 注解上的描述
                logEntity.setOperation(annotation.value());
                logType = annotation.type();
                logStoreType = annotation.storeType();
                if ("error".equalsIgnoreCase(annotation.scope()) && state == ErrorCode.SUCCESS) {
                    log.debug("LogOperationAspect only record error [{}]", annotation.value());
                    return;
                }
                if ("success".equalsIgnoreCase(annotation.scope()) && state != ErrorCode.SUCCESS) {
                    log.debug("LogOperationAspect only record success [{}]", annotation.value());
                    return;
                }

            }
        } catch (NoSuchMethodException ne) {
            log.error("日志切片未找到对应的方法", ne);
        }
        if ("login".equalsIgnoreCase(logType)) {
            // 登录日志
            try {
                JSONObject loginForm = JSONUtil.parseObj(params);
                logEntity.setTenantCode(loginForm.getStr("tenantCode"));
                if (StrUtil.endWith(loginForm.getStr("type"), "USERNAME_PASSWORD")) {
                    logEntity.setCreateName(loginForm.getStr("username"));
                } else if (StrUtil.endWith(loginForm.getStr("type"), "MOBILE_SMS")) {
                    logEntity.setCreateName(loginForm.getStr("mobile"));
                }
            } catch (Exception e2) {
                log.error("LogOperationAspect setCreateName error", e2);
            }
        } else {
            // 操作日志
            logEntity.setCreateName(ShiroUtils.getUserNamePretty());
            logEntity.setTenantCode(ShiroUtils.getUserTenantCode());
        }
        logEntity.setStoreType(logStoreType);
        logEntity.setState(state);
        logEntity.setRequestTime(time);
        logEntity.setType(logType);
        logEntity.setRequestBody(params);
        // 保存错误信息
        if (e != null) {
            logEntity.setContent(e instanceof OnexException ? e.toString() : ExceptionUtil.stacktraceToString(e));
        }
        // 请求参数
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (null != request) {
            logEntity.setUri(request.getRequestURI());
            logEntity.setRequestIp(HttpContextUtils.getIpAddr(request));
            // 对ip所在位置做处理,限制长度
            logEntity.setRequestIpRegion(logIp2Region ? StrUtil.sub(IpRegionUtil.getRegion(logEntity.getRequestIp()), 0, 200) : null);
            // 对ua做处理，限制长度300
            logEntity.setRequestUa(StrUtil.sub(request.getHeader(HttpHeaders.USER_AGENT), 0, 300));
            JSONObject requestParams = new JSONObject();
            requestParams.set("queryString", request.getQueryString());
            requestParams.set("url", request.getRequestURL());
            requestParams.set("method", request.getMethod());
            requestParams.set("contentType", request.getContentType());
            logEntity.setRequestParams(requestParams);
            /*if (request instanceof OnexHttpServletRequestWrapper) {
                try {
                    requestParams.set("params", IoUtil.read(request.getInputStream()).toString());
                } catch (IOException ie) {
                    log.error("读取流失败", e);
                }
            }*/
        }
        logService.saveLog(logEntity);
    }

    /**
     * 从joinPoint获取参数,其实是从方法的参数中获取，经过json解析后，会和实际传参有偏差
     * 不要试图处理所有的情况,尽量处理吧
     */
    private String getMethodParam(ProceedingJoinPoint joinPoint) {
        // 请求参数,接口方法中的参数,可能会有HttpServletRequest、HttpServletResponse、ModelMap
        Object[] args = joinPoint.getArgs();
        List<Object> actualParam = new ArrayList<>();
        for (Object arg : args) {
            // 只处理能处理的
            if (arg instanceof MultipartFile file) {
                actualParam.add(Dict.create().set("type", "file").set("name", file.getOriginalFilename()).set("size", file.getSize()));
            } else if (arg instanceof MultipartFile[] files) {
                List<Dict> list = new ArrayList<>();
                for (MultipartFile file : files) {
                    list.add(Dict.create().set("type", "file").set("name", file.getOriginalFilename()).set("size", file.getSize()));
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

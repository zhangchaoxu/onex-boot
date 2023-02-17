package com.nb6868.onex.common.log;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 链路日志拦截器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TraceLogInterceptor implements HandlerInterceptor {

    private final static String TRACE_ID = "TRACE_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // traceId可以由前端发起
        String traceId = StrUtil.emptyToDefault(request.getHeader(TRACE_ID), IdUtil.fastSimpleUUID());
        MDC.put(TRACE_ID, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(TRACE_ID);
    }
}

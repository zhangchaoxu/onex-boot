package com.nb6868.onex.common.log;

import cn.hutool.core.util.IdUtil;
import com.nb6868.onex.common.Const;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 链路日志拦截器
 * 注意：主线程中，如果使用了线程池，会导致线程池中丢失MDC信息；需要我们自己重写线程池，在调用线程跳动run之前，获取到主线程的MDC信息，重新put到子线程中的。
 * see <a href="https://blog.csdn.net/yangyanping20108/article/details/130410286">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class TraceLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put(Const.TRACE_ID, IdUtil.fastSimpleUUID());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.remove(Const.TRACE_ID);
    }
}

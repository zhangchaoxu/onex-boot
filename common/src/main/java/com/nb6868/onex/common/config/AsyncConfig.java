package com.nb6868.onex.common.config;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.log.BaseLogService;
import com.nb6868.onex.common.log.LogBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@EnableAsync
@ConditionalOnProperty(name = "onex.async.enable", havingValue = "true")
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Autowired
    TaskExecutionProperties taskExecutionProperties;

    @Autowired
    private BaseLogService logService;

    @Bean("AsyncExecutor")
    @Override
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize()); //核心线程数
        executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());  //最大线程数
        executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity()); //队列大小
        executor.setKeepAliveSeconds((int) taskExecutionProperties.getPool().getKeepAlive().getSeconds()); //线程最大空闲时间
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
        // 拒绝策略
        // AbortPolicy 丢弃任务，抛运行时异常
        // CallerRunsPolicy 执行任务
        // DiscardPolicy 忽视,什么都不会发生
        // DiscardOldestPolicy 从队列中踢出最先进入队列（最后一个执行）的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化executor
        executor.initialize();
        return executor;
    }

    /**
     * 注意,只会捕捉空返回值的异步方法
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            // 只会捕捉到空返回值的任务
            log.error("Async Exception method={}", method.getDeclaringClass().getName() + "." + method.getName());
            JSONArray paramArray = new JSONArray();
            for (Object param : objects) {
                log.info("Async Exception param={}", param);
                if (param instanceof Serializable) {
                    paramArray.put(param.toString());
                }
            }
            log.error("Async Exception message=", throwable);
            // 异常信息
            LogBody logBody = new LogBody();
            logBody.setStoreType("db");
            logBody.setType("error");
            logBody.setOperation("async");
            logBody.setUri(method.getDeclaringClass().getName() + "." + method.getName());
            logBody.setContent(ExceptionUtil.stacktraceToString(throwable));
            JSONObject requestParams = new JSONObject();
            requestParams.set("params", paramArray.size() > 0 ? paramArray.toString() : null);
            logBody.setRequestParams(requestParams);
            logService.saveLog(logBody);
        };
    }

}

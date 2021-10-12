package com.nb6868.onex.common.async;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 异步任务监控
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@Aspect
@Slf4j
public class AsyncTaskMonitor {
    @Autowired
    AsyncTaskManager manager;

    @Around("execution(* com.nb6868.onex.common.async.AsyncTaskExecutor.*(..))")
    public void taskHandle(ProceedingJoinPoint pjp) {
        // 记录开始执行时间
        TimeInterval timer = DateUtil.timer();
        // 获取taskId
        String taskId = pjp.getArgs()[1].toString();
        // 获取任务信息
        AsyncTaskInfo taskInfo = manager.getTaskInfo(taskId);
        log.info("AsyncTaskMonitor is monitoring async task:{}", taskId);
        taskInfo.setStatus(AsyncTaskStatusEnum.RUNNING);
        manager.setTaskInfo(taskInfo);
        AsyncTaskStatusEnum status;
        try {
            pjp.proceed();
            status = AsyncTaskStatusEnum.SUCCESS;
        } catch (Throwable throwable) {
            status = AsyncTaskStatusEnum.FAILED;
            log.error("AsyncTaskMonitor:async task {} is failed.Error info:{}", taskId, throwable.getMessage());
        }
        taskInfo.setEndTime(new Date());
        taskInfo.setStatus(status);
        taskInfo.setTimes(timer.intervalSecond());
        manager.setTaskInfo(taskInfo);
    }

}

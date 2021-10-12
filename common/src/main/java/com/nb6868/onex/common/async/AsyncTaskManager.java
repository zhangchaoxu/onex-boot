package com.nb6868.onex.common.async;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 异步任务管理器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class AsyncTaskManager {

    private Map<String, AsyncTaskInfo> taskContainer = new HashMap<>(16);

    @Autowired
    AsyncTaskExecutor asyncTaskExecutor;

    /**
     * 初始化任务
     */
    public AsyncTaskInfo initTask() {
        AsyncTaskInfo taskInfo = new AsyncTaskInfo();
        taskInfo.setTaskId(IdUtil.simpleUUID());
        taskInfo.setStatus(AsyncTaskStatusEnum.STARTED);
        taskInfo.setStartTime(new Date());
        setTaskInfo(taskInfo);
        return taskInfo;
    }

    /**
     * 初始化任务
     */
    public AsyncTaskInfo submit(AsyncTaskConstructor asyncTaskConstructor) {
        AsyncTaskInfo info = initTask();
        String taskId = info.getTaskId();
        asyncTaskExecutor.executor(asyncTaskConstructor,taskId);
        return info;
    }

    /**
     * 保存任务信息
     */
    public void setTaskInfo(AsyncTaskInfo taskInfo) {
        taskContainer.put(taskInfo.getTaskId(), taskInfo);
    }

    /**
     * 获取任务信息
     */
    public AsyncTaskInfo getTaskInfo(String taskId) {
        return taskContainer.get(taskId);
    }

    /**
     * 获取任务状态
     */
    public AsyncTaskStatusEnum getTaskStatus(String taskId) {
        return getTaskInfo(taskId).getStatus();
    }

}

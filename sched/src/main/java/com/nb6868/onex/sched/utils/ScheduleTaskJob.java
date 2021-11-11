package com.nb6868.onex.sched.utils;

import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.sched.entity.TaskLogEntity;
import com.nb6868.onex.sched.service.TaskLogService;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class ScheduleTaskJob extends ScheduleJob {

    @Override
    protected void saveTaskLog(TaskInfo task, long timeInterval, int state, String result) {
        TaskLogEntity logEntity = new TaskLogEntity();
        logEntity.setTaskId(Long.valueOf(task.getId()));
        logEntity.setTaskName(task.getName());
        logEntity.setParams(task.getParams().toString());
        logEntity.setTimes(timeInterval);
        logEntity.setState(state);
        logEntity.setResult(result);
        if (ErrorCode.JOB_NO_RUN == state) {
            // 指定结果不存db
            log.info("task log={}", JSONUtil.toJsonStr(logEntity));
        } else {
            TaskLogService taskLogService = SpringContextUtils.getBean(TaskLogService.class);
            taskLogService.save(logEntity);
        }
    }
}

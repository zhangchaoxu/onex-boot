package com.nb6868.onex.api.modules.sched.utils;

import com.nb6868.onex.api.modules.sched.entity.TaskLogEntity;
import com.nb6868.onex.api.modules.sched.service.TaskLogService;
import com.nb6868.onex.common.sched.ScheduleJob;
import com.nb6868.onex.common.sched.TaskInfo;
import com.nb6868.onex.common.util.SpringContextUtils;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ScheduleTaskJob extends ScheduleJob {

    @Override
    protected void saveTaskLog(TaskInfo task, long timeInterval, int state, String result) {
        // 获取spring bean
        TaskLogService taskLogService = SpringContextUtils.getBean(TaskLogService.class);
        TaskLogEntity log = new TaskLogEntity();
        log.setTaskId(Long.valueOf(task.getId()));
        log.setTaskName(task.getName());
        log.setParams(task.getParams().toString());
        log.setTimes(timeInterval);
        log.setState(state);
        log.setResult(result);
        taskLogService.save(log);
    }
}

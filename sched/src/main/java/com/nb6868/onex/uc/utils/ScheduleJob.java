package com.nb6868.onex.uc.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.util.SpringContextUtils;
import com.nb6868.onex.uc.SchedConst;
import com.nb6868.onex.uc.service.TaskLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class ScheduleJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        TaskInfo task = (TaskInfo) context.getMergedJobDataMap().get(SchedConst.JOB_PARAM_KEY);
        // 未指定环境或者当前环境包含了指定环境
        boolean activeContainAllow = StrUtil.isBlank(task.getEnv()) || ArrayUtil.containsAny(SpringContextUtils.getEnvironment().getActiveProfiles(), StrUtil.splitToArray(task.getEnv(), ","));
        if (activeContainAllow) {
            // 执行
            TaskLogService taskLogService = SpringContextUtils.getBean(TaskLogService.class);
            // 任务计时器
            TimeInterval timer = DateUtil.timer();
            Long taskLogId = 0L;
            try {
                // 执行任务
                log.debug("任务准备执行，任务ID：{}", task.getId());
                // 记录初始化日志
                taskLogId = taskLogService.saveLog(task, 0L, SchedConst.TaskLogState.INIT.getValue(), null);
                // 通过bean获取实现ITask的Task
                Object target = SpringContextUtils.getBean(task.getName());
                // 通过反射执行run方法
                Method method = target.getClass().getDeclaredMethod("run", TaskInfo.class, Long.class);
                ScheduleRunResult invokeResult = (ScheduleRunResult) method.invoke(target, task, taskLogId);
                // 保存完成结果日志
                if (!"db".equalsIgnoreCase(task.getLogType()) && invokeResult.getLogToDb()) {
                    task.setLogType("db");
                    taskLogService.saveLog(task, timer.interval(), SchedConst.TaskLogState.COMPLETED.getValue(), JSONUtil.toJsonStr(invokeResult.getResult()));
                } else {
                    taskLogService.updateLog(taskLogId, task, timer.interval(), SchedConst.TaskLogState.COMPLETED.getValue(), JSONUtil.toJsonStr(invokeResult.getResult()));
                }
                log.info("任务执行完毕，任务ID：{}", task.getId());
            } catch (Exception e) {
                log.error("任务执行失败，任务ID：{}", task.getId(), e);
                // 保存错误日志,发生错误强制存入db
                taskLogService.saveErrorLog(taskLogId, task, timer.interval(), ExceptionUtil.stacktraceToString(e));
            }
        } else {
            log.debug("非指定环境,不执行任务");
        }
    }

}

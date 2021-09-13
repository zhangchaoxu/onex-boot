package com.nb6868.onex.common.sched;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.SpringContextUtils;
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
        // 任务计时器
        TimeInterval timer = DateUtil.timer();
        // 任务执行状态
        int state = Const.ResultEnum.SUCCESS.value();
        // 任务执行结果
        String result = null;
        try {
            //执行任务
            log.info("任务准备执行，任务ID：{}", task.getId());
            Object target = SpringContextUtils.getBean(task.getName());
            Method method = target.getClass().getDeclaredMethod("run", String.class);
            Object invokeResult = method.invoke(target, task.getParams());
            result = invokeResult.toString();
            log.info("任务执行完毕，任务ID：{}", task.getId());
        } catch (Exception e) {
            //任务状态
            state = Const.ResultEnum.FAIL.value();
            // 错误消息
            result = ExceptionUtil.stacktraceToString(e);
            log.error("任务执行失败，任务ID：{}", task.getId(), e);
        } finally {
            saveTaskLog(task, timer.interval(), state, result);
        }
    }

    /**
     * 保存任务日志
     */
    protected void saveTaskLog(TaskInfo task, long timeInterval, int state, String result) {
        /*TaskLogService taskLogService = SpringContextUtils.getBean(TaskLogService.class);
        TaskLogEntity log = new TaskLogEntity();
        log.setTaskId(Long.valueOf(task.getId()));
        log.setTaskName(task.getName());
        log.setParams(task.getParams());
        log.setTimes(timeInterval);
        log.setState(state);
        log.setResult(result);
        taskLogService.save(log);*/
    }

}

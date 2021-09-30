package com.nb6868.onex.common.sched;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.InvocationTargetException;
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
            // 执行任务
            log.info("任务准备执行，任务ID：{}", task.getId());
            Object target = SpringContextUtils.getBean(task.getName());
            Method method = target.getClass().getDeclaredMethod("run", JSONObject.class);
            Object invokeResult = method.invoke(target, task.getParams());
            result = invokeResult.toString();
            log.info("任务执行完毕，任务ID：{}", task.getId());
        } catch (Exception e) {
            // 无法捕捉到OnexException,返回的是InvocationTargetException
            if (e instanceof InvocationTargetException && ((InvocationTargetException) e).getTargetException() instanceof OnexException) {
                OnexException onexException = (OnexException) ((InvocationTargetException) e).getTargetException();
                state = onexException.getCode();
                result = onexException.getMsg();
            } else {
                state = Const.ResultEnum.FAIL.value();
                result = ExceptionUtil.stacktraceToString(e);
            }
            log.error("任务执行失败，任务ID：{}", task.getId(), e);
        } finally {
            saveTaskLog(task, timer.interval(), state, result);
        }
    }

    /**
     * 保存任务日志
     */
    protected void saveTaskLog(TaskInfo task, long timeInterval, int state, String result) {
        /*// 获取spring bean
        TaskLogEntity logEntity = new TaskLogEntity();
        logEntity.setTaskId(Long.valueOf(task.getId()));
        logEntity.setTaskName(task.getName());
        logEntity.setParams(task.getParams().toString());
        logEntity.setTimes(timeInterval);
        logEntity.setState(state);
        logEntity.setError(result);
        if (ErrorCode.JOB_NO_RUN == state) {
            // 指定结果不存db
            log.info("task log={}", JSONUtil.toJsonStr(logEntity));
        } else {
            TaskLogService taskLogService = SpringContextUtils.getBean(TaskLogService.class);
            taskLogService.save(logEntity);
        }*/
    }

}

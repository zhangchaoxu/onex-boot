package com.nb6868.onex.common.sched;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class ScheduleJob extends QuartzJobBean {

    /**
     * 当前运行环境
     */
    @Value("${spring.profiles.active}")
    private String env;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        TaskInfo task = (TaskInfo) context.getMergedJobDataMap().get(SchedConst.JOB_PARAM_KEY);

        // 检查运行环境,若指定了运行环境,并且不包含当前运行环境,则跳出不执行
        if (StrUtil.isNotBlank(task.getRunEnv()) && !StrSplitter.splitTrim(task.getRunEnv(), ',', true).contains(env)) {
            log.info("task指定环境{},不匹配当前环境{}", task.getRunEnv(), env);
            return;
        }

        //任务开始时间
        TimeInterval timer = DateUtil.timer();
        long timeInterval = 0;
        int result = Const.ResultEnum.SUCCESS.value();
        String errorMsg = null;
        try {
            //执行任务
            log.info("任务准备执行，任务ID：{}", task.getId());
            Object target = SpringContextUtils.getBean(task.getName());
            Method method = target.getClass().getDeclaredMethod("run", String.class);
            method.invoke(target, task.getParams());

            //任务执行总时长
            timeInterval = timer.interval();
            //任务状态
            result = Const.ResultEnum.SUCCESS.value();
            log.info("任务执行完毕，任务ID：{}  总共耗时：{} 毫秒", task.getId(), timeInterval);
        } catch (Exception e) {
            //任务执行总时长
            //任务执行总时长
            timeInterval = timer.interval();
            //任务状态
            result = Const.ResultEnum.FAIL.value();
            // 错误消息
            errorMsg = ExceptionUtil.stacktraceToString(e);
            log.error("任务执行失败，任务ID：{}", task.getId(), e);
        } finally {
            saveTaskLog(task, timeInterval, result, errorMsg);
        }
    }

    /**
     * 保存任务日志
     */
    protected void saveTaskLog(TaskInfo task, long timeInterval, int result, String errorMsg) {
        // 获取spring bean
        /*TaskLogService taskLogService = SpringContextUtils.getBean(TaskLogService.class);
        TaskLogEntity log = new TaskLogEntity();
        log.setTaskId(task.getId());
        log.setTaskName(task.getName());
        log.setParams(task.getParams());
        log.setTimes(timeInterval);
        log.setState(result);
        log.setError(errorMsg);
        taskLogService.save(log);*/
    }

}

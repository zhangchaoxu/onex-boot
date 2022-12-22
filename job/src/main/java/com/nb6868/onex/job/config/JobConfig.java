package com.nb6868.onex.job.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.job.JobConst;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.entity.JobLogEntity;
import com.nb6868.onex.job.sched.AbstractJobRunService;
import com.nb6868.onex.job.service.JobLogService;
import com.nb6868.onex.job.service.JobService;
import com.nb6868.onex.job.sched.JobRunResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "onex.job.enable", havingValue = "true")
@Slf4j
public class JobConfig implements SchedulingConfigurer {

    @Autowired
    private JobService jobService;
    @Autowired
    private JobLogService jobLogService;

    /**
     * Spring初始化时候执行
     * 获取需要执行的任务，添加到定时器里
     *
     * @param taskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 从数据库中读取需要执行的任务列表
        jobService.query()
                .select("id")
                .list()
                .forEach(job -> initTrigger(taskRegistrar, job.getId()));
    }

    /**
     * 添加trigger
     */
    private void initTrigger(ScheduledTaskRegistrar taskRegistrar, Long jobId) {
        taskRegistrar.addTriggerTask(() -> {
            // 配置参数要再从数据库读一遍，否则不会变更
            JobEntity job = jobService.getById(jobId);
            if (job == null) {
                log.info("任务不存在，任务ID：{}", jobId);
            } else if (job.getState() != JobConst.JobState.NORMAL.getValue()) {
                log.info("任务状态异常，任务ID：{}，状态：{}", jobId, job.getState());
            } else {
                // 执行任务
                log.debug("任务准备执行，任务ID：{}", jobId);
                // 任务计时器
                TimeInterval timer = DateUtil.timer();
                // 记录初始化日志
                Long jobLogId = jobLogService.saveLog(job, 0L, JobConst.JobLogState.INIT.getValue(), null);
                // 通过bean获取实现Service
                JobRunResult runResult;
                AbstractJobRunService jobRunService;
                try {
                    // 通过bean获取实现Service
                    jobRunService = SpringUtil.getBean(job.getCode(), AbstractJobRunService.class);
                    runResult = jobRunService.run(job.getParams(), jobLogId);
                } catch (Exception e) {
                    log.error("任务执行失败，任务ID：{}", jobId, e);
                    // 保存错误日志,发生错误
                    if (jobLogId > 0) {
                        jobLogService.update().set("error", ExceptionUtil.stacktraceToString(e))
                                .set("time_interval", timer.interval())
                                .set("state", JobConst.JobLogState.ERROR.getValue())
                                .eq("id", jobLogId)
                                .update(new JobLogEntity());
                    }
                    return;
                }
                if (jobLogId == 0 && runResult.getLogToDb()) {
                    // 执行结果要求存入数据库
                    job.setLogType("db");
                    jobLogService.saveLog(job, timer.interval(), JobConst.JobLogState.COMPLETED.getValue(), JSONUtil.toJsonStr(runResult.getResult()));
                } else if (jobLogId > 0) {
                    jobLogService.update().set("result", JSONUtil.toJsonStr(runResult.getResult()))
                            .set("time_interval", timer.interval())
                            .set("state", JobConst.JobLogState.COMPLETED.getValue())
                            .eq("id", jobLogId)
                            .update(new JobLogEntity());
                }
                log.info("任务执行完毕，任务ID：{}", jobId);
            }
        }, triggerContext -> {
            log.info("TriggerTask next Trigger");
            // 配置参数要再从数据库读一遍，否则不会变更
            JobEntity job = jobService.getById(jobId);
            if (job != null && StrUtil.isNotBlank(job.getCron())) {
                return new CronTrigger(job.getCron()).nextExecutionTime(triggerContext);
            } else {
                return null;
            }
        });
    }

}

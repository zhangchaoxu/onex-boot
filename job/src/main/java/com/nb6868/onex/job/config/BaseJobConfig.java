package com.nb6868.onex.job.config;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.job.JobConst;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

/**
 * 基础job配置
 */
@Slf4j
public abstract class BaseJobConfig implements SchedulingConfigurer {

    @Autowired
    JobService jobService;

    /**
     * 添加trigger
     */
    protected void initTrigger(ScheduledTaskRegistrar taskRegistrar, Long jobId) {
        taskRegistrar.addTriggerTask(() -> {
            // 真正执行的时候,再从数据库判断一遍
            JobEntity job = jobService.getById(jobId);
            if (job == null) {
                log.error("job run Id=[{}], not found", jobId);
            } else if (job.getState() != JobConst.JobState.NORMAL.getValue()) {
                log.error("job run Id=[{}], state=[{}] abnormal", jobId, job.getState());
            } else {
                log.error("job run Id=[{}], go start", jobId);
                jobService.run(job, job.getParams());
            }
        }, triggerContext -> {
            // 配置参数要再从数据库读一遍，否则不会变更
            JobEntity job = jobService.getById(jobId);
            if (job == null) {
                log.error("job trigger Id=[{}], not found", jobId);
            } else if (StrUtil.isNotBlank(job.getCron())) {
                log.error("job trigger Id=[{}], cron is empty", jobId);
            } else if (CronExpression.isValidExpression(job.getCron())) {
                log.error("job trigger Id=[{}], cron=[{}] is not valid", jobId, job.getCron());
            } else {
                // 不管是否有效，加入下一个trigger
                log.error("job trigger Id=[{}], cron=[{}] add next execution", jobId, job.getCron());
                return new CronTrigger(job.getCron()).nextExecution(triggerContext);
            }
            return null;
        });
    }

}

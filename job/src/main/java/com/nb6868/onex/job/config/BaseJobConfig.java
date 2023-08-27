package com.nb6868.onex.job.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.job.JobConst;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;

@Slf4j
public abstract class BaseJobConfig implements SchedulingConfigurer {

    @Autowired
    private JobService jobService;

    /**
     * 添加trigger
     */
    protected void initTrigger(ScheduledTaskRegistrar taskRegistrar, Long jobId) {
        taskRegistrar.addTriggerTask(() -> {
            // 配置参数要再从数据库读一遍，否则不会变更
            JobEntity job = jobService.getById(jobId);
            if (job == null) {
                log.info("任务不存在，任务ID：{}", jobId);
            } else if (job.getState() != JobConst.JobState.NORMAL.getValue()) {
                log.info("任务状态异常，任务ID：{}，状态：{}", jobId, job.getState());
            } else {
                jobService.run(job, job.getParams());
            }
        }, triggerContext -> {
            log.info("TriggerTask next Trigger");
            // 配置参数要再从数据库读一遍，否则不会变更
            JobEntity job = jobService.getById(jobId);
            if (ObjectUtil.isNotNull(job) && StrUtil.isNotBlank(job.getCron())) {
                return new CronTrigger(job.getCron()).nextExecutionTime(triggerContext);
            } else {
                return null;
            }
        });
    }

}

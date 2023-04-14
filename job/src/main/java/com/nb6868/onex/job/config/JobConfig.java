package com.nb6868.onex.job.config;

import com.nb6868.onex.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "onex.job.enable", havingValue = "true")
@Slf4j
public class JobConfig extends BaseJobConfig {

    @Autowired
    private JobService jobService;

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

}

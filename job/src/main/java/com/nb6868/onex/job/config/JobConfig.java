package com.nb6868.onex.job.config;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "onex.job.enable", havingValue = "true")
@Slf4j
public class JobConfig extends BaseJobConfig {

    @Autowired
    JobService jobService;

    @Value("${onex.job.code-prefix}")
    String jobCodePrefix;

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
                .likeRight(StrUtil.isNotBlank(jobCodePrefix), "code", jobCodePrefix)
                .select("id")
                .list()
                .forEach(job -> initTrigger(taskRegistrar, job.getId()));
    }

    @PostConstruct
    public void init() {
        log.info("job enable run with code prefix={}", jobCodePrefix);
    }

}

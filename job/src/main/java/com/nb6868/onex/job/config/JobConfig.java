package com.nb6868.onex.job.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.service.JobService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;
import java.util.concurrent.Executors;

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
     * @param taskRegistrar register
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 从数据库中读取需要执行的任务列表,不区分状态,因为可能会修改状态
        List<JobEntity> jobList = jobService.query()
                .likeRight(StrUtil.isNotBlank(jobCodePrefix), "code", jobCodePrefix)
                .select("id")
                .list();
        log.info("job run has [{}] jobs", jobList.size());
        if (CollUtil.isNotEmpty(jobList)) {
            // 设定一个长度size的定时任务线程池,@Scheduled 默认是单线程,当任务同时间时会冲突
            taskRegistrar.setScheduler(Executors.newScheduledThreadPool(jobList.size()));
            // 按照id初始化job
            jobList.forEach(job -> initTrigger(taskRegistrar, job.getId()));
        }
    }

    @PostConstruct
    public void init() {
        log.info("job enable run with code prefix={}", jobCodePrefix);
    }

}

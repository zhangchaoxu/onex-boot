package com.nb6868.onex.job.service;

import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.job.dao.JobLogDao;
import com.nb6868.onex.job.dto.JobLogDTO;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.entity.JobLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@Slf4j
public class JobLogService extends DtoService<JobLogDao, JobLogEntity, JobLogDTO> {

    /**
     * 保存记录
     */
    public Long saveLog(JobEntity job, long timeInterval, int state, String result) {
        JobLogEntity logEntity = new JobLogEntity();
        logEntity.setJobId(job.getId());
        logEntity.setJobCode(job.getCode());
        logEntity.setTenantCode(job.getTenantCode());
        logEntity.setParams(job.getParams());
        logEntity.setTimeInterval(timeInterval);
        logEntity.setState(state);
        logEntity.setResult(result);
        if ("db".equalsIgnoreCase(job.getLogType())) {
            // 存入数据库
            save(logEntity);
            return logEntity.getId();
        } else {
            // 不存入数据库
            log.info("task log={}", JSONUtil.toJsonStr(logEntity));
            return 0L;
        }
    }

}

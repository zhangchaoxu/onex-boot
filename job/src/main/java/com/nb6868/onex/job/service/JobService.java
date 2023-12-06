package com.nb6868.onex.job.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.job.JobConst;
import com.nb6868.onex.job.dao.JobDao;
import com.nb6868.onex.job.dto.JobDTO;
import com.nb6868.onex.job.dto.JobRunWithParamsForm;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.entity.JobLogEntity;
import com.nb6868.onex.job.sched.AbstractJobRunService;
import com.nb6868.onex.job.sched.JobRunResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@Slf4j
public class JobService extends DtoService<JobDao, JobEntity, JobDTO> {

    @Autowired
    private JobLogService jobLogService;

    /**
     * 修改状态
     */
    public boolean changeState(List<Long> ids, int state) {
        return SqlHelper.retBool(getBaseMapper().update(new JobEntity(), new UpdateWrapper<JobEntity>().set("state", state).in("id", ids)));
    }

    /**
     * 立即执行
     */
    @Transactional(rollbackFor = Exception.class)
    public void runWithParams(JobRunWithParamsForm form) {
        JobEntity job = getById(form.getId());
        AssertUtils.isNull(job, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 忽略是否停止
        this.run(job, form.getParams());
    }

    public void run(@NotNull JobEntity job, JSONObject runParams) {
        log.debug("任务准备执行，任务ID：{}", job.getId());
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
            runResult = jobRunService.run(runParams, jobLogId);
        } catch (Exception e) {
            log.error("任务执行失败，任务ID：{}", job.getId(), e);
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
            jobLogService.saveLog(job, timer.interval(), JobConst.JobLogState.COMPLETED.getValue(), runResult.getResult().toString());
        } else if (jobLogId > 0) {
            jobLogService.update()
                    .set("result", runResult.getResult().toString())
                    .set("time_interval", timer.interval())
                    .set("state", JobConst.JobLogState.COMPLETED.getValue())
                    .eq("id", jobLogId)
                    .update(new JobLogEntity());
        }
        log.info("任务执行完毕，任务ID：{}", job.getId());
    }

}

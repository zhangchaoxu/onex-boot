package com.nb6868.onex.job.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.job.JobConst;
import com.nb6868.onex.job.dao.JobDao;
import com.nb6868.onex.job.dto.JobDTO;
import com.nb6868.onex.job.dto.JobRunWithParamsReq;
import com.nb6868.onex.job.dto.JobSaveOrUpdateReq;
import com.nb6868.onex.job.entity.JobEntity;
import com.nb6868.onex.job.entity.JobLogEntity;
import com.nb6868.onex.job.sched.AbstractJobRunService;
import com.nb6868.onex.job.sched.JobRunRes;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    JobLogService jobLogService;

    /**
     * 新增或修改
     */
    @Transactional(rollbackFor = Exception.class)
    public JobEntity saveOrUpdateByReq(JobSaveOrUpdateReq req) {
        // 检查请求
        // 转换数据格式
        JobEntity entity;
        if (req.hasId()) {
            // 编辑数据
            entity = getById(req.getId());
            AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
            BeanUtil.copyProperties(req, entity);
        } else {
            // 新增数据
            entity = BeanUtil.copyProperties(req, JobEntity.class);
        }
        // 处理数据
        boolean ret = saveOrUpdateById(entity);
        AssertUtils.isFalse(ret, "数据更新保存失败");
        return entity;
    }

    /**
     * 通过code获得job
     *
     * @param code jobcode
     */
    public JobEntity getByCode(@NotNull String code) {
        return lambdaQuery().eq(JobEntity::getCode, code)
                .last(Const.LIMIT_ONE)
                .one();
    }

    /**
     * 修改状态
     */
    public boolean changeState(List<Long> ids, int state) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        return lambdaUpdate().set(JobEntity::getState, state)
                .in(ids.size() > 1, JobEntity::getId, ids)
                .eq(ids.size() == 1, JobEntity::getId, ids.get(0))
                .update();
    }

    /**
     * 立即执行
     */
    @Transactional(rollbackFor = Exception.class)
    public Long runWithParams(JobRunWithParamsReq form) {
        JobEntity job = getById(form.getId());
        AssertUtils.isNull(job, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 忽略是否停止
        return this.run(job, form.getParams());
    }

    public Long run(@NotNull JobEntity job, JSONObject runParams) {
        return run(job, null, runParams);
    }

    public Long run(@NotNull JobEntity job, Long jobLogId, JSONObject runParams) {
        log.debug("任务准备执行，任务ID：{}", job.getId());
        // 任务计时器
        TimeInterval timer = DateUtil.timer();
        // 记录初始化日志,若没有指定logId,就生成一个
        if (null == jobLogId || 0L == jobLogId) {
            jobLogId = jobLogService.saveLog(job, 0L, JobConst.JobLogStateEnum.INIT.getCode(), null);
        }
        // 通过bean获取实现Service
        JobRunRes runResult;
        AbstractJobRunService jobRunService;
        try {
            // 通过bean获取实现Service
            jobRunService = SpringUtil.getBean(job.getCode(), AbstractJobRunService.class);
            runResult = jobRunService.run(runParams, jobLogId);
        } catch (Exception e) {
            log.error("任务执行失败，任务ID：{}", job.getId(), e);
            // 保存错误日志,发生错误
            if (jobLogId > 0) {
                jobLogService.lambdaUpdate()
                        .eq(JobLogEntity::getId, jobLogId)
                        .set(JobLogEntity::getError, ExceptionUtil.stacktraceToString(e))
                        .set(JobLogEntity::getTimeInterval, timer.interval())
                        .set(JobLogEntity::getState, JobConst.JobLogStateEnum.ERROR.getCode())
                        .update(new JobLogEntity());
            }
            return jobLogId;
        }
        if (jobLogId == 0 && runResult.getLogToDb()) {
            // 执行结果要求存入数据库
            job.setLogType("db");
            jobLogService.saveLog(job, timer.interval(), JobConst.JobLogStateEnum.COMPLETED.getCode(), runResult.getResult().toString());
        } else if (jobLogId > 0) {
            jobLogService.lambdaUpdate()
                    .eq(JobLogEntity::getId, jobLogId)
                    .set(JobLogEntity::getResult, runResult.getResult().toString())
                    .set(JobLogEntity::getTimeInterval, timer.interval())
                    .set(JobLogEntity::getState, JobConst.JobLogStateEnum.COMPLETED.getCode())
                    .update(new JobLogEntity());
        }
        log.info("任务执行完毕，任务ID：{}", job.getId());
        return jobLogId;
    }

}

package com.nb6868.onex.sys.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.sys.SchedConst;
import com.nb6868.onex.sys.dao.SchedLogDao;
import com.nb6868.onex.sys.dto.SchedLogDTO;
import com.nb6868.onex.sys.entity.SchedLogEntity;
import com.nb6868.onex.sys.utils.SchedTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@Slf4j
public class SchedLogService extends DtoService<SchedLogDao, SchedLogEntity, SchedLogDTO> {

    @Override
    public QueryWrapper<SchedLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<SchedLogEntity>(new QueryWrapper<>(), params)
                .like("taskName", "task_name")
                .eq("taskId", "task_id")
                .getQueryWrapper();
    }

    /**
     * 保存记录
     */
    public long saveLog(SchedTask taskInfo, long timeInterval, int state, String result) {
        SchedLogEntity logEntity = new SchedLogEntity();
        logEntity.setSchedId(taskInfo.getId());
        logEntity.setSchedName(taskInfo.getName());
        logEntity.setTenantCode(taskInfo.getTenantCode());
        logEntity.setParams(taskInfo.getParams());
        logEntity.setTimes(timeInterval);
        logEntity.setState(state);
        logEntity.setResult(result);
        if ("db".equalsIgnoreCase(taskInfo.getLogType())) {
            // 存入数据库
            save(logEntity);
            return logEntity.getId();
        } else {
            // 不存入数据库
            log.info("task log={}", JSONUtil.toJsonStr(logEntity));
            return 0L;
        }
    }

    /**
     * 更新记录
     */
    public void updateLog(Long taskLogId, SchedTask taskInfo, long times, int state, String result) {
        if (taskLogId > 0) {
            // 更新
            update().set("result", result)
                    .set("times", times)
                    .set("state", state)
                    .eq("id", taskLogId)
                    .update(new SchedLogEntity());
        } else {
            log.info("task log update, taskName={},times={},state={},result={}", taskInfo.getName(), times, state, result);
        }
    }

    /**
     * 保存错误日志
     */
    public void saveErrorLog(Long taskLogId, SchedTask taskInfo, Long times, String error) {
        if (taskLogId > 0) {
            // 更新
            update().set("error", error)
                    .set("times", times)
                    .set("state", SchedConst.SchedLogState.ERROR.getValue())
                    .eq("id", taskLogId)
                    .update(new SchedLogEntity());
        } else {
            // 保存
            SchedLogEntity logEntity = new SchedLogEntity();
            logEntity.setSchedId(taskInfo.getId());
            logEntity.setSchedName(taskInfo.getName());
            logEntity.setTenantCode(taskInfo.getTenantCode());
            logEntity.setParams(taskInfo.getParams());
            logEntity.setTimes(times);
            logEntity.setState(SchedConst.SchedLogState.ERROR.getValue());
            logEntity.setError(error);
            save(logEntity);
        }
    }

}

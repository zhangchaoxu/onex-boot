package com.nb6868.onex.uc.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.uc.SchedConst;
import com.nb6868.onex.uc.dao.TaskLogDao;
import com.nb6868.onex.uc.dto.TaskLogDTO;
import com.nb6868.onex.uc.entity.TaskLogEntity;
import com.nb6868.onex.uc.utils.TaskInfo;
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
public class TaskLogService extends DtoService<TaskLogDao, TaskLogEntity, TaskLogDTO> {

    @Override
    public QueryWrapper<TaskLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<TaskLogEntity>(new QueryWrapper<>(), params)
                .like("taskName", "task_name")
                .eq("taskId", "task_id")
                .getQueryWrapper();
    }

    /**
     * 保存记录
     */
    public long saveLog(TaskInfo task, long timeInterval, int state, String result) {
        TaskLogEntity logEntity = new TaskLogEntity();
        logEntity.setTaskId(task.getId());
        logEntity.setTaskName(task.getName());
        logEntity.setParams(task.getParams().toString());
        logEntity.setTimes(timeInterval);
        logEntity.setState(state);
        logEntity.setResult(result);
        if ("db".equalsIgnoreCase(task.getLogType())) {
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
    public void updateLog(Long taskLogId, TaskInfo task, long times, int state, String result) {
        if (taskLogId > 0) {
            // 更新
            update().set("result", result)
                    .set("times", times)
                    .set("state", state)
                    .eq("id", taskLogId)
                    .update(new TaskLogEntity());
        } else {
            log.info("task log update, taskName={},times={},state={},result={}", task.getName(), times, state, result);
        }
    }

    /**
     * 保存错误日志
     */
    public void saveErrorLog(Long taskLogId, TaskInfo taskInfo, Long times, String error) {
        if (taskLogId > 0) {
            // 更新
            update().set("error", error)
                    .set("times", times)
                    .set("state", SchedConst.TaskLogState.ERROR.getValue())
                    .eq("id", taskLogId)
                    .update(new TaskLogEntity());
        } else {
            // 保存
            TaskLogEntity logEntity = new TaskLogEntity();
            logEntity.setTaskId(taskInfo.getId());
            logEntity.setTaskName(taskInfo.getName());
            logEntity.setParams(taskInfo.getParams().toString());
            logEntity.setTimes(times);
            logEntity.setState(SchedConst.TaskLogState.ERROR.getValue());
            logEntity.setError(error);
            save(logEntity);
        }
    }

}

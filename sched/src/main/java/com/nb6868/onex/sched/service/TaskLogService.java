package com.nb6868.onex.sched.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.sched.dao.TaskLogDao;
import com.nb6868.onex.sched.dto.TaskLogDTO;
import com.nb6868.onex.sched.entity.TaskLogEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TaskLogService extends DtoService<TaskLogDao, TaskLogEntity, TaskLogDTO> {

    @Override
    public QueryWrapper<TaskLogEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<TaskLogEntity>(new QueryWrapper<>(), params)
                .like("taskName", "task_name")
                .eq("taskId", "task_id")
                .getQueryWrapper();
    }

}

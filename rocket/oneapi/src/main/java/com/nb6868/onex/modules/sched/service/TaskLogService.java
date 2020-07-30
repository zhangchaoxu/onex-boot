package com.nb6868.onex.modules.sched.service;

import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.service.BaseService;
import com.nb6868.onex.modules.sched.dto.TaskLogDTO;
import com.nb6868.onex.modules.sched.entity.TaskLogEntity;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface TaskLogService extends BaseService<TaskLogEntity> {

	PageData<TaskLogDTO> page(Map<String, Object> params);

	TaskLogDTO get(Long id);
}

package com.nb6868.onexboot.api.modules.sched.service;

import com.nb6868.onexboot.api.modules.sched.dto.TaskLogDTO;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.service.BaseService;
import com.nb6868.onexboot.api.modules.sched.entity.TaskLogEntity;

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

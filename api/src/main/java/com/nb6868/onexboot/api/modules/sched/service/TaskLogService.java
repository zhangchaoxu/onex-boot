package com.nb6868.onexboot.api.modules.sched.service;

import com.nb6868.onexboot.api.modules.sched.dto.TaskLogDTO;
import com.nb6868.onexboot.api.modules.sched.entity.TaskLogEntity;
import com.nb6868.onexboot.common.service.CrudService;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface TaskLogService extends CrudService<TaskLogEntity, TaskLogDTO> {

}

package com.nb6868.onexboot.api.modules.sched.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sched.dao.TaskLogDao;
import com.nb6868.onexboot.api.modules.sched.dto.TaskLogDTO;
import com.nb6868.onexboot.api.modules.sched.entity.TaskLogEntity;
import com.nb6868.onexboot.api.modules.sched.service.TaskLogService;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TaskLogServiceImpl extends CrudServiceImpl<TaskLogDao, TaskLogEntity, TaskLogDTO> implements TaskLogService {

	@Override
	public QueryWrapper<TaskLogEntity> getWrapper(String method, Map<String, Object> params) {
		return new WrapperUtils<TaskLogEntity>(new QueryWrapper<>(), params)
				.like("taskName", "task_name")
				.eq("taskId", "task_id")
				.getQueryWrapper();
	}

}

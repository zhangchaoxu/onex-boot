package com.nb6868.onexboot.api.modules.sched.service.impl;

import com.nb6868.onexboot.api.modules.sched.dao.TaskLogDao;
import com.nb6868.onexboot.api.modules.sched.dto.TaskLogDTO;
import com.nb6868.onexboot.api.modules.sched.entity.TaskLogEntity;
import com.nb6868.onexboot.api.modules.sched.service.TaskLogService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.service.impl.BaseServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TaskLogServiceImpl extends BaseServiceImpl<TaskLogDao, TaskLogEntity> implements TaskLogService {

	@Override
	public PageData<TaskLogDTO> page(Map<String, Object> params) {
		IPage<TaskLogEntity> page = baseMapper.selectPage(
			getPage(params, Const.CREATE_DATE, false),
			getWrapper(params)
		);
		return getPageData(page, TaskLogDTO.class);
	}

	private QueryWrapper<TaskLogEntity> getWrapper(Map<String, Object> params){
		String jobId = (String) params.get("jobId");

		QueryWrapper<TaskLogEntity> wrapper = new QueryWrapper<>();
		wrapper.eq(StringUtils.isNotBlank(jobId), "job_id", jobId);

		return wrapper;
	}

	@Override
	public TaskLogDTO get(Long id) {
		TaskLogEntity entity = baseMapper.selectById(id);

		return ConvertUtils.sourceToTarget(entity, TaskLogDTO.class);
	}

}

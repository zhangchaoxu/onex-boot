package com.nb6868.onex.modules.sched.service.impl;

import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.pojo.PageData;
import com.nb6868.onex.booster.service.impl.BaseServiceImpl;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.modules.sched.dao.TaskLogDao;
import com.nb6868.onex.modules.sched.dto.TaskLogDTO;
import com.nb6868.onex.modules.sched.entity.TaskLogEntity;
import com.nb6868.onex.modules.sched.service.TaskLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

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
		String jobId = (String)params.get("jobId");

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

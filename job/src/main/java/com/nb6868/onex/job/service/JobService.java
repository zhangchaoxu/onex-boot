package com.nb6868.onex.job.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.job.dao.JobDao;
import com.nb6868.onex.job.dto.JobDTO;
import com.nb6868.onex.job.dto.JobRunWithParamsForm;
import com.nb6868.onex.job.entity.JobEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class JobService extends DtoService<JobDao, JobEntity, JobDTO> {

	/**
	 * 修改状态
	 */
	public boolean changeState(List<Long> ids, int state) {
		return SqlHelper.retBool(getBaseMapper().update(new JobEntity(), new UpdateWrapper<JobEntity>().set("state", state).in("id", ids)));
	}

	/**
	 * 立即执行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runWithParams(JobRunWithParamsForm form) {
		JobEntity task = getById(form.getId());
		AssertUtils.isNull(task, ErrorCode.DB_RECORD_NOT_EXISTED);

		// SchedTask taskInfo = getTaskInfoFromTask(task);
		// ScheduleUtils.run(scheduler, taskInfo);
	}

}

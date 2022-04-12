package com.nb6868.onex.sched.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sched.SchedConst;
import com.nb6868.onex.sched.dao.TaskDao;
import com.nb6868.onex.sched.dto.TaskDTO;
import com.nb6868.onex.sched.dto.TaskRunWithParamsForm;
import com.nb6868.onex.sched.entity.TaskEntity;
import com.nb6868.onex.sched.utils.ScheduleJob;
import com.nb6868.onex.sched.utils.ScheduleUtils;
import com.nb6868.onex.sched.utils.TaskInfo;
import org.quartz.Scheduler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TaskService extends DtoService<TaskDao, TaskEntity, TaskDTO> {

	@Autowired
	private Scheduler scheduler;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveDto(TaskDTO dto) {
		// 检查name是否已存在
		AssertUtils.isTrue(hasDuplicated(null, "name", dto.getName()), ErrorCode.ERROR_REQUEST, "任务名已存在");

		TaskEntity entity = ConvertUtils.sourceToTarget(dto, TaskEntity.class);
		boolean ret = save(entity);
		if (ret) {
			// copy主键值到dto
			BeanUtils.copyProperties(entity, dto);
			// 创建job
			ScheduleUtils.createScheduleJob(ScheduleJob.class, scheduler, getTaskInfoFromTask(entity));
		}
		return ret;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateDto(TaskDTO dto) {
		// 检查name是否已存在
		AssertUtils.isTrue(hasDuplicated(dto.getId(), "name", dto.getName()), ErrorCode.ERROR_REQUEST, "任务名已存在");
		TaskEntity entity = ConvertUtils.sourceToTarget(dto, TaskEntity.class);
		boolean ret = updateById(entity);
		if (ret) {
			// 更新job
			ScheduleUtils.updateScheduleJob(scheduler, getTaskInfoFromTask(entity));
		}
		return ret;
	}

	public boolean logicDeleteByIds(List<Long> idList) {
		boolean ret = super.logicDeleteByIds(idList);
		// 删除任务
		idList.forEach(id -> ScheduleUtils.deleteScheduleJob(scheduler, id));
		return ret;
	}

	/**
	 * 修改状态
	 */
	public boolean changeState(List<Long> ids, int state) {
		return SqlHelper.retBool(getBaseMapper().update(new TaskEntity(), new UpdateWrapper<TaskEntity>().set("state", state).in("id", ids)));
	}

	/**
	 * 立即执行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runWithParams(TaskRunWithParamsForm requestBody) {
		TaskInfo taskInfo = getTaskInfoFromTask(getById(requestBody.getId()));
		if (StrUtil.isNotBlank(requestBody.getParams())) {
			JSONObject params = null;
			try {
				params = JSONUtil.parseObj(requestBody.getParams());
			} catch (JSONException e) {
				log.error("序列化参数失败", e);
			} finally {
				taskInfo.setParams(params);
			}
		}
		ScheduleUtils.run(scheduler, taskInfo);
	}

	/**
	 * 暂停运行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void pause(List<Long> ids) {
		ids.forEach(id -> ScheduleUtils.pauseJob(scheduler, id));

		changeState(ids, SchedConst.TaskState.PAUSE.getValue());
	}

	/**
	 * 恢复运行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void resume(List<Long> ids) {
		for (Long id : ids) {
			ScheduleUtils.resumeJob(scheduler, id);
		}

		changeState(ids, SchedConst.TaskState.NORMAL.getValue());
	}

	/**
	 * 从TaskEntity获得TaskInfo
	 */
	private TaskInfo getTaskInfoFromTask(TaskEntity taskEntity) {
		return ConvertUtils.sourceToTarget(taskEntity, TaskInfo.class);
	}
}

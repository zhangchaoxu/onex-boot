package com.nb6868.onexboot.api.modules.sched.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onexboot.api.modules.sched.SchedConst;
import com.nb6868.onexboot.api.modules.sched.dao.TaskDao;
import com.nb6868.onexboot.api.modules.sched.dto.TaskDTO;
import com.nb6868.onexboot.api.modules.sched.entity.TaskEntity;
import com.nb6868.onexboot.api.modules.sched.utils.ScheduleUtils;
import com.nb6868.onexboot.common.service.DtoService;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.quartz.Scheduler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
	public QueryWrapper<TaskEntity> getWrapper(String method, Map<String, Object> params) {
		return new WrapperUtils<TaskEntity>(new QueryWrapper<>(), params)
				.like("name", "name")
				.eq("state", "state")
				.getQueryWrapper();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveDto(TaskDTO dto) {
		TaskEntity entity = ConvertUtils.sourceToTarget(dto, TaskEntity.class);
		boolean ret = save(entity);
		// copy主键值到dto
		BeanUtils.copyProperties(entity, dto);
		//
		ScheduleUtils.createScheduleJob(scheduler, entity);
		return ret;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateDto(TaskDTO dto) {
		TaskEntity entity = ConvertUtils.sourceToTarget(dto, TaskEntity.class);
		boolean ret = updateById(entity);
		//
		ScheduleUtils.updateScheduleJob(scheduler, entity);
		return ret;
	}

	@Override
	public boolean logicDeleteByIds(Collection<? extends Serializable> idList) {
		boolean ret = super.logicDeleteByIds(idList);
		// 删除任务
		idList.forEach((Consumer<Serializable>) serializable -> ScheduleUtils.deleteScheduleJob(scheduler, (Long) serializable));
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
	public void run(List<Long> ids) {
		for (Long id : ids) {
			ScheduleUtils.run(scheduler, this.getById(id));
		}
	}

	/**
	 * 暂停运行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void pause(List<Long> ids) {
		for (Long id : ids) {
			ScheduleUtils.pauseJob(scheduler, id);
		}

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
}

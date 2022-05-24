package com.nb6868.onex.sys.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.SchedConst;
import com.nb6868.onex.sys.dao.SchedDao;
import com.nb6868.onex.sys.dto.SchedDTO;
import com.nb6868.onex.sys.dto.SchedRunWithParamsForm;
import com.nb6868.onex.sys.entity.SchedEntity;
import com.nb6868.onex.sys.utils.ScheduleJob;
import com.nb6868.onex.sys.utils.ScheduleUtils;
import com.nb6868.onex.sys.utils.SchedTask;
import org.quartz.Scheduler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SchedService extends DtoService<SchedDao, SchedEntity, SchedDTO> {

	@Autowired
	private Scheduler scheduler;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveDto(SchedDTO dto) {
		// 检查name是否已存在
		AssertUtils.isTrue(hasDuplicated(null, "name", dto.getName()), ErrorCode.ERROR_REQUEST, "任务名已存在");

		SchedEntity entity = ConvertUtils.sourceToTarget(dto, SchedEntity.class);
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
	public boolean updateDto(SchedDTO dto) {
		// 检查name是否已存在
		AssertUtils.isTrue(hasDuplicated(dto.getId(), "name", dto.getName()), ErrorCode.ERROR_REQUEST, "任务名已存在");
		SchedEntity entity = ConvertUtils.sourceToTarget(dto, SchedEntity.class);
		boolean ret = updateById(entity);
		if (ret) {
			// 更新job
			ScheduleUtils.updateScheduleJob(scheduler, getTaskInfoFromTask(entity));
		}
		return ret;
	}

	/**
	 * 删除任务
	 * @param id 任务id
	 * @return
	 */
	public boolean delete(Long id) {
		boolean ret = super.logicDeleteById(id);
		// 删除任务
		ScheduleUtils.deleteScheduleJob(scheduler, id);
		return ret;
	}

	/**
	 * 修改状态
	 */
	public boolean changeState(List<Long> ids, int state) {
		return SqlHelper.retBool(getBaseMapper().update(new SchedEntity(), new UpdateWrapper<SchedEntity>().set("state", state).in("id", ids)));
	}

	/**
	 * 立即执行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void runWithParams(SchedRunWithParamsForm requestBody) {
		SchedTask taskInfo = getTaskInfoFromTask(getById(requestBody.getId()));
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

		changeState(ids, SchedConst.SchedState.PAUSE.getValue());
	}

	/**
	 * 恢复运行
	 */
	@Transactional(rollbackFor = Exception.class)
	public void resume(List<Long> ids) {
		for (Long id : ids) {
			ScheduleUtils.resumeJob(scheduler, id);
		}
		changeState(ids, SchedConst.SchedState.NORMAL.getValue());
	}

	/**
	 * 从TaskEntity获得TaskInfo
	 */
	private SchedTask getTaskInfoFromTask(SchedEntity taskEntity) {
		return ConvertUtils.sourceToTarget(taskEntity, SchedTask.class);
	}
}

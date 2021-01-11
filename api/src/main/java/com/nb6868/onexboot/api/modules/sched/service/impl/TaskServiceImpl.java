package com.nb6868.onexboot.api.modules.sched.service.impl;

import com.nb6868.onexboot.api.modules.sched.SchedConst;
import com.nb6868.onexboot.api.modules.sched.dao.TaskDao;
import com.nb6868.onexboot.api.modules.sched.dto.TaskDTO;
import com.nb6868.onexboot.api.modules.sched.entity.TaskEntity;
import com.nb6868.onexboot.api.modules.sched.service.TaskService;
import com.nb6868.onexboot.api.modules.sched.utils.ScheduleUtils;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
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
public class TaskServiceImpl extends CrudServiceImpl<TaskDao, TaskEntity, TaskDTO> implements TaskService {

    @Autowired
    private Scheduler scheduler;

    @Override
    public QueryWrapper<TaskEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<TaskEntity>(new QueryWrapper<>(), params)
                .like("name", "name")
                .eq("status", "status")
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

	@Override
	public boolean changeStatus(List<Long> ids, int status) {
		return SqlHelper.retBool(getBaseMapper().update(new TaskEntity(), new UpdateWrapper<TaskEntity>().set("status", status).in("id", ids)));
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(List<Long> ids) {
        for (Long id : ids) {
            ScheduleUtils.run(scheduler, this.getById(id));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pause(List<Long> ids) {
        for (Long id : ids) {
            ScheduleUtils.pauseJob(scheduler, id);
        }

		changeStatus(ids, SchedConst.TaskStatus.PAUSE.getValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resume(List<Long> ids) {
        for (Long id : ids) {
            ScheduleUtils.resumeJob(scheduler, id);
        }

		changeStatus(ids, SchedConst.TaskStatus.NORMAL.getValue());
    }

}

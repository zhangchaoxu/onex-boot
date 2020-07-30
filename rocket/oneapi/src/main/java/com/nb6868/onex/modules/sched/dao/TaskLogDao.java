package com.nb6868.onex.modules.sched.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.sched.entity.TaskLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TaskLogDao extends BaseDao<TaskLogEntity> {

}

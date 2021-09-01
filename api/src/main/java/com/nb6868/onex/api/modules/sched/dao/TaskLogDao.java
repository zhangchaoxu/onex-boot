package com.nb6868.onex.api.modules.sched.dao;

import com.nb6868.onex.api.modules.sched.entity.TaskLogEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TaskLogDao extends BaseDao<TaskLogEntity> {

}

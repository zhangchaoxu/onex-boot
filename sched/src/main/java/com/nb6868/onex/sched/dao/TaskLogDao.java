package com.nb6868.onex.sched.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.sched.entity.TaskLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TaskLogDao extends BaseDao<TaskLogEntity> {

}

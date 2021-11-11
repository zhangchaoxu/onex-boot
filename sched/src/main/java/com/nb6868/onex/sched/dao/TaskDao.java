package com.nb6868.onex.sched.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.sched.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TaskDao extends BaseDao<TaskEntity> {

}

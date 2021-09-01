package com.nb6868.onex.api.modules.sched.dao;

import com.nb6868.onex.api.modules.sched.entity.TaskEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TaskDao extends BaseDao<TaskEntity> {

}

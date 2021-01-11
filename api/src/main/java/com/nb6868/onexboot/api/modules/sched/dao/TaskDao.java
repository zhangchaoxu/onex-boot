package com.nb6868.onexboot.api.modules.sched.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.sched.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface TaskDao extends BaseDao<TaskEntity> {

}

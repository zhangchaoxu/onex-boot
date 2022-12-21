package com.nb6868.onex.job.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.job.entity.JobLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface JobLogDao extends BaseDao<JobLogEntity> {

}

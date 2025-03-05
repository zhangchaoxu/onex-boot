package com.nb6868.onex.job.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.job.entity.JobItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-定时任务明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface JobItemDao extends BaseDao<JobItemEntity> {

}

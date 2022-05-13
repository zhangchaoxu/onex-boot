package com.nb6868.onex.sys.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.sys.entity.SchedLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface SchedLogDao extends BaseDao<SchedLogEntity> {

}

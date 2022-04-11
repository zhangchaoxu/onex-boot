package com.nb6868.onex.sys.dao;

import com.nb6868.onex.sys.entity.LogEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface LogDao extends BaseDao<LogEntity> {

}

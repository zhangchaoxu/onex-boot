package com.nb6868.onex.sys.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.sys.entity.CalendarEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 万年历
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface CalendarDao extends BaseDao<CalendarEntity> {

}

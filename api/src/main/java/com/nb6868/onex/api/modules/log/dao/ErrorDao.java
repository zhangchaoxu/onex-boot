package com.nb6868.onex.api.modules.log.dao;

import com.nb6868.onex.api.modules.log.entity.ErrorEntity;
import com.nb6868.onex.common.jpa.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 异常日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface ErrorDao extends BaseDao<ErrorEntity> {

}

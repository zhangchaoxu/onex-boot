package com.nb6868.onexboot.api.modules.log.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.log.entity.OperationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OperationDao extends BaseDao<OperationEntity> {

}

package com.nb6868.onex.shop.modules.log.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.shop.modules.log.entity.OperationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OperationDao extends BaseDao<OperationEntity> {

}

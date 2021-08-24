package com.nb6868.onex.api.modules.shop.dao;

import com.nb6868.onex.api.modules.shop.entity.OrderLogEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderLogDao extends BaseDao<OrderLogEntity> {

}

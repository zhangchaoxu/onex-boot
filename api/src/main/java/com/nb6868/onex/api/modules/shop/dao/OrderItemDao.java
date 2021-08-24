package com.nb6868.onex.api.modules.shop.dao;

import com.nb6868.onex.api.modules.shop.entity.OrderItemEntity;
import com.nb6868.onex.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderItemDao extends BaseDao<OrderItemEntity> {

}

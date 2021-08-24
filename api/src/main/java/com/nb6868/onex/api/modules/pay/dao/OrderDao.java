package com.nb6868.onex.api.modules.pay.dao;

import com.nb6868.onex.common.dao.BaseDao;
import com.nb6868.onex.api.modules.pay.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {

}

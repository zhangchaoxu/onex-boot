package com.nb6868.onex.modules.shop.dao;

import com.nb6868.onex.booster.dao.BaseDao;
import com.nb6868.onex.modules.shop.entity.OrderLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderLogDao extends BaseDao<OrderLogEntity> {

}

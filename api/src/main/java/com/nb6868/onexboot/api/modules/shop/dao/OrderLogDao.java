package com.nb6868.onexboot.api.modules.shop.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.shop.entity.OrderLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderLogDao extends BaseDao<OrderLogEntity> {

}

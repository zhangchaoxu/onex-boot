package com.nb6868.onexboot.api.modules.shop.dao;

import com.nb6868.onexboot.api.modules.shop.entity.OrderEntity;
import com.nb6868.onexboot.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {

}

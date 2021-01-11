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
    /**
     * 查询收益排行自定义SQL
     *
     * @param goodsId 商品ID
     * @return result
     */
    @Select("select benefit_user_id,sum(benefit_price) as sum from shop_order_item where deleted=0 and goods_id=#{goodsId} GROUP BY benefit_user_id having sum >0 ORDER BY sum desc limit 20")
    List<Map> benefitRanking(@Param("goodsId") Long goodsId);
}

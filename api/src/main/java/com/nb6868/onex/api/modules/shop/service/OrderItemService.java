package com.nb6868.onex.api.modules.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.shop.dao.OrderItemDao;
import com.nb6868.onex.api.modules.shop.dto.OrderItemDTO;
import com.nb6868.onex.api.modules.shop.entity.OrderItemEntity;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderItemService extends DtoService<OrderItemDao, OrderItemEntity, OrderItemDTO> {

    @Override
    public QueryWrapper<OrderItemEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderItemEntity>(new QueryWrapper<>(), params)
                .eq("orderId", "user_id")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    /**
     * 获取订单下的所有产品
     *
     * @param orderId 订单号id
     * @return
     */
    public List<OrderItemDTO> getDtoListByOrderId(Long orderId) {
        return ConvertUtils.sourceToTarget(query().eq("order_id", orderId).list(), OrderItemDTO.class);
    }

    /**
     * 删除订单下的所有产品
     *
     * @param orderId 订单号id
     * @return
     */
    public boolean deleteByOrderId(Long orderId) {
        return logicDeleteByWrapper(new QueryWrapper<OrderItemEntity>().eq("order_id", orderId));
    }

}

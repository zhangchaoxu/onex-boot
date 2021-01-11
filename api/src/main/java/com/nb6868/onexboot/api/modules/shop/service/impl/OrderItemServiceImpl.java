package com.nb6868.onexboot.api.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.shop.dao.OrderItemDao;
import com.nb6868.onexboot.api.modules.shop.dto.OrderItemDTO;
import com.nb6868.onexboot.api.modules.shop.entity.OrderItemEntity;
import com.nb6868.onexboot.api.modules.shop.service.OrderItemService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.CrudServiceImpl;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.WrapperUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderItemServiceImpl extends CrudServiceImpl<OrderItemDao, OrderItemEntity, OrderItemDTO> implements OrderItemService {

    @Override
    public QueryWrapper<OrderItemEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderItemEntity>(new QueryWrapper<>(), params)
                .eq("orderId", "user_id")
                .eq("tenantId", "tenant_id")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public List<OrderItemDTO> getDtoListByOrderId(Long orderId) {
        return ConvertUtils.sourceToTarget(query().eq("order_id", orderId).list(), OrderItemDTO.class);
    }

    @Override
    public boolean deleteByOrderId(Long orderId) {
        return logicDeleteByWrapper(new QueryWrapper<OrderItemEntity>().eq("order_id", orderId));
    }

}

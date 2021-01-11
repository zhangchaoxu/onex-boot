package com.nb6868.onexboot.api.modules.shop.service;

import com.nb6868.onexboot.api.modules.shop.dto.OrderItemDTO;
import com.nb6868.onexboot.api.modules.shop.entity.OrderItemEntity;
import com.nb6868.onexboot.common.service.CrudService;

import java.util.List;

/**
 * 订单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface OrderItemService extends CrudService<OrderItemEntity, OrderItemDTO> {

    /**
     * 获取订单下的所有产品
     *
     * @param orderId 订单号id
     * @return
     */
    List<OrderItemDTO> getDtoListByOrderId(Long orderId);

    /**
     * 删除订单下的所有产品
     *
     * @param orderId 订单号id
     * @return
     */
    boolean deleteByOrderId(Long orderId);

}

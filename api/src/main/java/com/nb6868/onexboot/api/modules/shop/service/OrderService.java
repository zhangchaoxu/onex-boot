package com.nb6868.onexboot.api.modules.shop.service;

import com.nb6868.onexboot.api.modules.pay.dto.PayRequest;
import com.nb6868.onexboot.api.modules.shop.dto.OrderChangeReceiverRequest;
import com.nb6868.onexboot.api.modules.shop.dto.OrderDTO;
import com.nb6868.onexboot.api.modules.shop.dto.OrderOneClickRequest;
import com.nb6868.onexboot.api.modules.shop.entity.OrderEntity;
import com.nb6868.onexboot.common.pojo.ChangeStateRequest;
import com.nb6868.onexboot.common.service.CrudService;

import java.io.Serializable;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {

    /**
     * 通过订单号获取订单
     */
    OrderEntity getByNo(String no);

    /**
     * 修改订单收件信息
     */
    boolean changeReceiver(OrderChangeReceiverRequest request);

    /**
     * 生成订单号
     *
     * @param policy 生成策略
     * @param prefix 订单前缀
     */
    String generateOrderNo(String policy, String prefix);

    /**
     * 取消订单
     *
     * @param orderId 订单id
     * @param remark 备注
     */
    boolean cancel(Long orderId, String remark);

    /**
     * 取消超时未支付订单
     *
     * @param second 超时秒数
     */
    boolean cancelUnPaidOrder(long second);


    boolean checkOrder(Long orderId);

    /**
     * 一键下单
     */
    OrderEntity oneClick(OrderOneClickRequest request);

    /**
     * 订单支付,返回支付参数
     */
    Serializable pay(PayRequest payRequest);

    /**
     * 取消并退款
     */
    boolean cancelAndRefund(ChangeStateRequest request);

    /**
     * 退款
     */
    boolean refund(Long id);

    /**
     * 支付回调
     *
     * @param payOrder 支付订单
     */
    boolean payNotify(com.nb6868.onexboot.api.modules.pay.entity.OrderEntity payOrder);

}

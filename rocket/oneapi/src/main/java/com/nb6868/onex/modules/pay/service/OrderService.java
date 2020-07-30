package com.nb6868.onex.modules.pay.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.nb6868.onex.booster.service.CrudService;
import com.nb6868.onex.modules.pay.dto.OrderDTO;
import com.nb6868.onex.modules.pay.entity.OrderEntity;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {

    /**
     * 处理微信支付通知
     *
     * @param result 微信支付通知
     * @return 处理结果
     */
    boolean handleWxNotifyResult(WxPayOrderNotifyResult result) throws WxPayException;

    /**
     * 通过orderId获得支付订单
     *
     * @param orderTable 订单表名
     * @param orderId    订单id
     * @param payType    支付类型
     * @return 支付订单
     */
    OrderEntity getByOrderId(String orderTable, Long orderId, int payType);

}

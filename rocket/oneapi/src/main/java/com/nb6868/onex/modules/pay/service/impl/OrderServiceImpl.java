package com.nb6868.onex.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.DateUtils;
import com.nb6868.onex.booster.util.WrapperUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.pay.PayConst;
import com.nb6868.onex.modules.pay.dao.OrderDao;
import com.nb6868.onex.modules.pay.dto.OrderDTO;
import com.nb6868.onex.modules.pay.entity.OrderEntity;
import com.nb6868.onex.modules.pay.service.OrderService;
import com.nb6868.onex.modules.pay.util.PayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service("PayOrderService")
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

    @Autowired
    com.nb6868.onex.modules.shop.service.OrderService shopOrderService;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderEntity>(new QueryWrapper<>(), params)
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public boolean handleWxNotifyResult(WxPayOrderNotifyResult notifyResult) throws WxPayException {
        // 商家数据包,存入payOrderId
        String payOrderId = notifyResult.getAttach();
        AssertUtils.isEmpty(payOrderId, "缺少支付订单信息");

        // 验证支付订单信息
        OrderEntity payOrder = getById(payOrderId);
        AssertUtils.isNull(payOrder, "缺少支付订单");
        AssertUtils.isEmpty(payOrder.getChannelParam(), "缺少支付配置");

        // 验证数据包合法性
        WxPayService wxPayService = PayUtils.getWxPayServiceByParam(payOrder.getChannelParam());
        notifyResult.checkResult(wxPayService, wxPayService.getConfig().getSignType(), true);
        // 检查支付订单状态
        if (payOrder.getStatus() == PayConst.PayStatusEnum.NO_PAY.value()) {
            // 根据不同的table,更新不同的模块类型
            boolean orderPayNotify = false;
            if ("shop_order".equalsIgnoreCase(payOrder.getOrderTable())) {
                // 更新商城订单
                orderPayNotify = shopOrderService.payNotify(payOrder);
            } else if ("ips_order".equalsIgnoreCase(payOrder.getOrderTable())) {
                // 更新ips order 状态
            }
            // 支付订单生成,待处理->支付成功/支付成功->业务处理完成
            update().eq("id", payOrder.getId())
                    .set("status", orderPayNotify ? PayConst.PayStatusEnum.BIZ_HANDLED.value() : PayConst.PayStatusEnum.PAID.value())
                    .set("end_time", DateUtils.parse(notifyResult.getTimeEnd(), "yyyyMMddHHmmss"))
                    .set("transaction_id", notifyResult.getTransactionId())
                    .set("total_fee", notifyResult.getTotalFee())
                    .set("currency", notifyResult.getFeeType())
                    .apply("notify_count = notify_count + 1")
                    .update(new OrderEntity());
            return true;
        } else {
            update().eq("id", payOrder.getId())
                    .apply("notify_count = notify_count + 1")
                    .update(new OrderEntity());
            return true;
        }
    }

    @Override
    public OrderEntity getByOrderId(String orderTable, Long orderId, int payType) {
        return query().eq("order_table", orderTable)
                .eq("order_id", orderId)
                .eq("pay_type", payType)
                .last(Const.LIMIT_ONE).one();
    }
}

package com.nb6868.onex.api.modules.pay.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.nb6868.onex.api.modules.pay.PayConst;
import com.nb6868.onex.api.modules.pay.dao.OrderDao;
import com.nb6868.onex.api.modules.pay.dto.OrderDTO;
import com.nb6868.onex.api.modules.pay.entity.OrderEntity;
import com.nb6868.onex.api.modules.pay.util.PayUtils;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.util.WrapperUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service("PayOrderService")
public class OrderService extends DtoService<OrderDao, OrderEntity, OrderDTO> {

    @Autowired
    com.nb6868.onex.api.modules.shop.service.OrderService shopOrderService;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderEntity>(new QueryWrapper<>(), params)
                .eq("orderNo", "order_no")
                .eq("orderTable", "order_table")
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    /**
     * 处理微信支付通知
     *
     * @param notifyResult 微信支付通知
     * @return 处理结果
     */
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
        if (payOrder.getState() == PayConst.PayStateEnum.NO_PAY.value()) {
            // 根据不同的table,更新不同的模块类型
            boolean orderPayNotify = false;
            if ("shop_order".equalsIgnoreCase(payOrder.getOrderTable())) {
                // 更新商城订单
                orderPayNotify = shopOrderService.payNotify(payOrder);
            }
            // 支付订单生成,待处理->支付成功/支付成功->业务处理完成
            update().eq("id", payOrder.getId())
                    .set("state", orderPayNotify ? PayConst.PayStateEnum.BIZ_HANDLED.value() : PayConst.PayStateEnum.PAID.value())
                    .set("end_time", DateUtil.parse(notifyResult.getTimeEnd(), DatePattern.PURE_DATETIME_FORMAT))
                    .set("transaction_id", notifyResult.getTransactionId())
                    .set("total_fee", notifyResult.getTotalFee())
                    .set("currency", notifyResult.getFeeType())
                    .setSql("notify_count = notify_count + 1")
                    .update(new OrderEntity());
            return true;
        } else {
            update().eq("id", payOrder.getId())
                    .setSql("notify_count = notify_count + 1")
                    .update(new OrderEntity());
            return true;
        }
    }

    /**
     * 通过orderId获得支付订单
     *
     * @param orderTable 订单表名
     * @param orderId    订单id
     * @param payType    支付类型
     * @return 支付订单
     */
    public OrderEntity getByOrderId(String orderTable, Long orderId, int payType) {
        return query().eq("order_table", orderTable)
                .eq("order_id", orderId)
                .eq("pay_type", payType)
                .last(Const.LIMIT_ONE).one();
    }

}

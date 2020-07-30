package com.nb6868.onex.modules.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.pojo.ChangeStatusRequest;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.*;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.msg.dto.MailSendRequest;
import com.nb6868.onex.modules.msg.service.MailLogService;
import com.nb6868.onex.modules.pay.PayConst;
import com.nb6868.onex.modules.pay.dto.PayRequest;
import com.nb6868.onex.modules.pay.entity.ChannelEntity;
import com.nb6868.onex.modules.pay.service.ChannelService;
import com.nb6868.onex.modules.pay.util.PayUtils;
import com.nb6868.onex.modules.shop.ShopConst;
import com.nb6868.onex.modules.shop.dao.OrderDao;
import com.nb6868.onex.modules.shop.dto.*;
import com.nb6868.onex.modules.shop.entity.GoodsEntity;
import com.nb6868.onex.modules.shop.entity.OrderEntity;
import com.nb6868.onex.modules.shop.entity.OrderItemEntity;
import com.nb6868.onex.modules.shop.entity.OrderLogEntity;
import com.nb6868.onex.modules.shop.service.GoodsService;
import com.nb6868.onex.modules.shop.service.OrderItemService;
import com.nb6868.onex.modules.shop.service.OrderLogService;
import com.nb6868.onex.modules.shop.service.OrderService;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.dto.UserDTO;
import com.nb6868.onex.modules.uc.entity.UserEntity;
import com.nb6868.onex.modules.uc.service.UserService;
import com.nb6868.onex.modules.uc.user.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

    @Autowired
    OrderLogService orderLogService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    MailLogService mailLogService;
    @Autowired
    ParamService paramService;
    @Autowired
    com.nb6868.onex.modules.pay.service.OrderService payOrderService;
    @Autowired
    ChannelService channelService;
    @Autowired
    UserService userService;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<OrderEntity>(new QueryWrapper<>(), params)
                .like("no", "no")
                .eq("userId", "user_id")
                .eq("status", "status")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .eq("tenantId", "tenant_id")
                .and("search", queryWrapper -> {
                    String search = (String) params.get("search");
                    queryWrapper.like("no", search);
                })
                .and("receiverSearch", queryWrapper -> {
                    String search = (String) params.get("receiverSearch");
                    queryWrapper.like("receiver_consignee", search)
                            .or().like("receiver_mobile", search)
                            .or().like("receiver_address", search)
                            .or().like("receiver_region_name", search);
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Override
    public OrderEntity getByNo(String no) {
        return query().eq("no", no).last(Const.LIMIT_ONE).one();
    }

    @Override
    public boolean changeReceiver(OrderChangeReceiverRequest request) {
        return false;
    }

    @Override
    public String generateOrderNo(String policy, String prefix) {
        String orderNo;
        if ("DATE_RANDOM".equalsIgnoreCase(policy)) {
            // 订单号规则：前缀+日期+随机数
            String time = DateUtils.format(DateUtils.now(), "yyyyMMdd");
            orderNo = prefix + time + StringUtils.getRandomDec(6);
        } else if ("DATETIME_RANDOM".equalsIgnoreCase(policy)) {
            // 订单号规则：前缀+日期时间+随机数
            String time = DateUtils.format(DateUtils.now(), "yyyyMMddHHmmss");
            orderNo = prefix + time + StringUtils.getRandomDec(6);
        } else {
            throw new OnexException("不支持的订单号生成策略");
        }

        // 判断订单编号是否已存在
        if (hasDuplicated(null, "no", orderNo)) {
            return generateOrderNo(policy, prefix);
        } else {
            return orderNo;
        }
    }

    @Override
    public boolean cancelUnPaidOrder(long second) {
        // 这里不直接update是因为执行过程中有可能订单状态已经发生变化，而又没有锁单，会导致订单其实已支付又被系统取消
        List<OrderEntity> orders = query().eq("pay_status", PayConst.PayTypeEnum.NO_PAY.value())
                .apply("order_time < DATE_SUB(NOW(), interval " + second + " second)")
                .list();
        for (OrderEntity order : orders) {
            // 更新订单状态
            boolean ret = update().eq("id", order.getId())
                    .set("status", ShopConst.OrderStatusEnum.CANCELED.value())
                    .update(new OrderEntity());
            if (ret) {
                // 返回扣除的积分

                // 给会员加上积分
                // 插入日志
                OrderLogEntity orderLog = new OrderLogEntity();
                orderLog.setOrderId(order.getId());
                orderLog.setType(ShopConst.OrderStatusEnum.CANCELED.value());
                orderLog.setContent("定时任务取消超时未支付订单");
                orderLogService.save(orderLog);
            } else {
                // 因为顺序执行的时间差,有可能部分订单已经被支付了
            }
        }
        return false;
    }

    @Override
    public boolean checkOrder(Long orderId) {
        update().eq("id", orderId).set("status", ShopConst.OrderStatusEnum.CANCELED.value()).update(new OrderEntity());
       /* OrderEntity orderEntity =query().eq("status", ShopConst.OrderStatusEnum.PLACED.value())
                .eq("id", orderId)
                .apply("place_time < DATE_SUB(NOW(), interval " + second + " second)")
                .one();*/

        return false;
    }

    @Override
    public boolean cancel(Long id, String remark) {
        // 检查订单和状态
        OrderEntity order = getById(id);
        AssertUtils.isNull(order, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isTrue(order.getStatus() == ShopConst.OrderStatusEnum.CANCELED.value(), "订单已取消");

        boolean ret = update().eq("id", order.getId())
                .set("status", ShopConst.OrderStatusEnum.CANCELED.value())
                .update(new OrderEntity());
        if (ret) {
            // 返回扣除的积分
            if (order.getPointsDeduct() > 0) {

            }
            // 给会员加上积分
            // 插入日志
            OrderLogEntity orderLog = new OrderLogEntity();
            orderLog.setOrderId(order.getId());
            orderLog.setType(ShopConst.OrderStatusEnum.CANCELED.value());
            orderLog.setContent(remark);
            orderLogService.save(orderLog);
            return true;
        } else {
            // 因为顺序执行的时间差,有可能部分订单已经被支付了
            return false;
        }
    }

    @Override
    public OrderEntity oneClick(OrderOneClickRequest request) {
        // 验证商品信息与库存
        GoodsEntity goods = goodsService.getById(request.getGoodsId());
        AssertUtils.isNull(goods, "商品不存在");
        AssertUtils.isTrue(goods.getMarketable() != 1, "购买商品已下架");
        AssertUtils.isTrue(request.getQty().compareTo(goods.getStock()) > 0, "商品库存不足");
        AssertUtils.isTrue(goods.getQtyMin().compareTo(new BigDecimal(-1)) != 0 && request.getQty().compareTo(goods.getQtyMin()) < 0, "购买量低于起购量:" + goods.getQtyMin());
        AssertUtils.isTrue(goods.getQtyMax().compareTo(new BigDecimal(-1)) != 0 && request.getQty().compareTo(goods.getQtyMax()) > 0, "购买量超过限购量:" + goods.getQtyMax());
        // todo 检查商品限购策略

        // 存入order
        OrderEntity order = ConvertUtils.sourceToTarget(request, OrderEntity.class);
        order.setNo(generateOrderNo("DATE_RANDOM", ""));
        order.setStatus(ShopConst.OrderStatusEnum.PLACED.value());
        order.setPayStatus(ShopConst.OrderPayStatusEnum.NO_PAY.value());
        order.setBuyType("oneclick");
        order.setGoodsPrice(goods.getSalePrice().multiply(request.getQty()));
        order.setTotalPrice(goods.getSalePrice());
        order.setPayPrice(goods.getSalePrice());
        order.setExpressPrice(new BigDecimal(0));
        order.setPayType(request.getPayType());
        order.setOrderTime(DateUtils.now());
        order.setGoodsDetail(goods.getName() + request.getQty());
        order.setUserId(SecurityUser.getUserId());
        order.setTenantId(goods.getTenantId());
        order.setTenantName(goods.getTenantName());
        order.setGoodsDiscountPrice(new BigDecimal("0"));
        if (null != request.getBenefitUserId() && 0 != request.getBenefitUserId()) {
            order.setBenefitUserId(request.getBenefitUserId());
            order.setBenefitStatus(0);
        }
        save(order);

        // 存入orderItem
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setGoodsQty(request.getQty());
        orderItem.setGoodsId(goods.getId());
        orderItem.setGoodsName(goods.getName());
        orderItem.setGoodsCover(goods.getImgs());
        orderItem.setGoodsPrice(goods.getSalePrice());
        orderItem.setGoodTotalPrice(goods.getSalePrice().multiply(request.getQty()));
        orderItem.setOrderId(order.getId());
        orderItem.setTenantId(goods.getTenantId());
        orderItem.setTenantName(goods.getTenantName());
        orderItem.setGoodsDiscountPrice(new BigDecimal("0"));
        orderItem.setGoodsTotalDiscountPrice(new BigDecimal("0"));
        if (null != request.getBenefitUserId() && 0 != request.getBenefitUserId()) {
            orderItem.setBenefitUserId(request.getBenefitUserId());
            orderItem.setBenefitStatus(0);
        }
        orderItemService.save(orderItem);

        // 插入订单日志
        OrderLogEntity orderLog = new OrderLogEntity();
        orderLog.setOrderId(order.getId());
        orderLog.setOrderNo(order.getNo());
        orderLog.setType(1);
        orderLog.setContent("一键下单");
        orderLog.setTenantId(goods.getTenantId());
        orderLog.setTenantName(goods.getTenantName());
        orderLogService.save(orderLog);

        return order;
    }

    @Override
    public Serializable pay(PayRequest payRequest) {
        // 检查订单及状态
        OrderEntity order = getById(payRequest.getOrderId());
        AssertUtils.isNull(order, ErrorCode.DB_RECORD_NOT_EXISTED);
        AssertUtils.isFalse(order.getStatus() == ShopConst.OrderStatusEnum.PLACED.value(), "订单状态不允许支付");
        AssertUtils.isFalse(order.getPayStatus() == ShopConst.OrderPayStatusEnum.NO_PAY.value(), "订单支付状态不允许支付");

        // 支付渠道
        ChannelEntity channel = channelService.getByTenantIdAndPayType(order.getTenantId(), payRequest.getPayType());
        AssertUtils.isNull(channel, "不支持该支付渠道");

        // 存入PayOrder
        // 判断payorder是否存在
        com.nb6868.onex.modules.pay.entity.OrderEntity payOrder = payOrderService.getByOrderId("shop_order", payRequest.getOrderId(), order.getPayType());
        if (null == payOrder) {
            payOrder = new com.nb6868.onex.modules.pay.entity.OrderEntity();
            payOrder.setChannelId(channel.getId());
            payOrder.setChannelParam(channel.getParam());
            payOrder.setOrderId(order.getId());
            payOrder.setOrderNo(order.getNo());
            payOrder.setOrderTable("shop_order");
            payOrder.setNotifyUrl(channel.getNotifyUrl());
            payOrder.setTenantId(order.getTenantId());
            payOrder.setTenantName(order.getTenantName());
            payOrder.setStatus(0);
            payOrderService.save(payOrder);
        }

        // 获取支付服务
        if (payRequest.getPayType().toUpperCase().startsWith("WECHAT")) {
            // 微信支付,唤起微信统一下单接口
            // 统一下单(详见https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1)
            // 再次支付请求参数和第一次保持一致,否则会提示201商户订单号重复
            WxPayService wxPayService = PayUtils.getWxPayServiceByParam(channel.getParam());
            // 发起支付
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            // 解决重复问题
            orderRequest.setOutTradeNo(order.getNo() + "_" + DateUtils.format(DateUtils.now(), "yyyyHHddHHmmssSSS"));
            orderRequest.setOpenid(payRequest.getOpenid());
            orderRequest.setTradeType(wxPayService.getConfig().getTradeType());
            orderRequest.setDeviceInfo("WEB");
            // 支付回调地址
            orderRequest.setNotifyUrl(channel.getNotifyUrl());
            orderRequest.setBody(order.getGoodsDetail());
            orderRequest.setProductId(order.getId().toString());
            // 塞入支付订单号,回调时候也会返回
            orderRequest.setAttach(payOrder.getId().toString());
            // 元转成分
            orderRequest.setTotalFee(order.getTotalPrice().multiply(new BigDecimal(100)).intValue());
            orderRequest.setSpbillCreateIp(HttpContextUtils.getIpAddr(HttpContextUtils.getHttpServletRequest()));
            try {
                return wxPayService.createOrder(orderRequest);
            } catch (WxPayException e) {
                e.printStackTrace();
                throw new OnexException(ErrorCode.WX_API_ERROR, e.getErrCodeDes());
            }
        } else {
            throw new OnexException("暂不支持的支付类型:" + order.getPayType());
        }
    }

    @Override
    public boolean cancelAndRefund(ChangeStatusRequest request) {
        OrderEntity order = getById(request.getId());
        AssertUtils.isNull(order, "订单不存在");
        AssertUtils.isFalse(order.isSysCancelable(), "订单不允许取消");
        AssertUtils.isFalse(order.isSysRefundable(), "订单不允许退款");

        boolean ret = update().eq("id", request.getId()).set("status", ShopConst.OrderStatusEnum.CANCELED.value()).update(new OrderEntity());
        AssertUtils.isFalse(ret, "更新订单状态失败");

        // 发起退款
        /*try {
            WxPayRefundResult result = getWxService("WX_PAY_CFG_FISH").refund(request);
            return new Result<>().success(result);
        } catch (WxPayException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.WX_API_ERROR, e);
        }*/
        // 加入日志
        return false;
    }

    @Override
    public boolean payNotify(com.nb6868.onex.modules.pay.entity.OrderEntity payOrder) {
        OrderEntity orderEntity = getById(payOrder.getOrderId());
        if (orderEntity != null && orderEntity.getPayStatus() == ShopConst.OrderPayStatusEnum.NO_PAY.value()) {
            // 短信下发
            MailSendRequest smsSendRequest = new MailSendRequest();
            smsSendRequest.setMailTo(orderEntity.getReceiverMobile());
            smsSendRequest.setTplCode("FISH_ORDER");
            smsSendRequest.setContentParam("{\"code\":\"" + orderEntity.getNo() + "\",\"user\":\"" + orderEntity.getReceiverConsignee() + "\"}");
            mailLogService.send(smsSendRequest);

            // 订单收益计算
            System.out.println("收到支付回调数据");
            this.calculateIncome(orderEntity.getId());

            // 更新数据
            return update().set("status", ShopConst.OrderStatusEnum.PAID.value())
                    .set("pay_status", ShopConst.OrderPayStatusEnum.PAID.value())
                    .set("pay_price", new BigDecimal(payOrder.getTotalFee()).divide(new BigDecimal(100), RoundingMode.HALF_UP))
                    .set("pay_time", payOrder.getEndTime())
                    .set("pay_type", payOrder.getId())
                    .eq("id", orderEntity.getId())
                    .update(new OrderEntity());
        } else {
            return false;
        }
    }

    @Override
    public boolean refund(Long id) {
        OrderEntity order = getById(id);
        // 检查订单
        AssertUtils.isNull(order, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 检查订单支付状态
        AssertUtils.isFalse(order.getPayStatus() == ShopConst.OrderPayStatusEnum.PAID.value(), "订单支付状态不允许退款");
        // 找到payOrder
        com.nb6868.onex.modules.pay.entity.OrderEntity payOrder = payOrderService.getById(1L);
        AssertUtils.isNull(payOrder, "支付订单不存在");

        WxPayService wxPayService = PayUtils.getWxPayServiceByParam(payOrder.getChannelParam());
        // 发起退款
        try {
            WxPayRefundRequest refundRequest = new WxPayRefundRequest();
            refundRequest.setTransactionId(payOrder.getTransactionId());
            refundRequest.setTotalFee(payOrder.getTotalFee());
            refundRequest.setRefundFee(payOrder.getTotalFee());
            refundRequest.setOutRefundNo("T_" + payOrder.getOrderNo());
            wxPayService.refund(refundRequest);
            return true;
        } catch (WxPayException e) {
            e.printStackTrace();
            throw new OnexException(ErrorCode.WX_API_ERROR, e);
        }
    }

    @Override
    public boolean calculateIncome(Long orderId) {
        try {
            OrderEntity orderEntity = getById(orderId);
            if (null == orderEntity) {
                System.out.println("订单收益支付程序：订单未找到，ID-" + orderId);
                return false;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("orderId", orderEntity.getId());
            List<OrderItemDTO> itemDTOList = orderItemService.listDto(params);
            BigDecimal receiptorAmountSum = new BigDecimal("0");
            for (OrderItemDTO dto : itemDTOList) {
                // 判断是否有收益变更标记
                boolean flag = false;
                // 获取身份信息
                if (null == dto.getBenefitUserId()) {
                    // 无收益人情况
                    continue;
                }
                UserDTO benefitUser = userService.getDtoById(dto.getBenefitUserId());
                if (null == benefitUser) {
                    System.out.println("订单支付回调程序：收益人未找到！订单明细ID:" + dto.getId());
                    continue;
                }
                // 验证该单收益是否已被计算
                if (null != dto.getBenefitStatus() && 0 != dto.getBenefitStatus()) {
                    System.out.println("订单支付回调程序：订单收益收到重复计算请求！订单明细ID:" + dto.getId());
                    continue;
                }
                // 验证产品信息，是否开启分销模式
                BigDecimal receiptorAmount = new BigDecimal("0");
                BigDecimal receiptorAmountOne = new BigDecimal("0");
                GoodsDTO goodsDTO = goodsService.getDtoById(dto.getGoodsId());
                if (null != goodsDTO.getDistType() && 0 != goodsDTO.getDistType().intValue()) {
                    switch (goodsDTO.getDistType().intValue()) {
                        case 1:
                            // 按比例
                            receiptorAmountOne = goodsDTO.getSalePrice().multiply(goodsDTO.getDistScale()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                            break;
                        case 2:
                            // 固定金额
                            receiptorAmountOne = goodsDTO.getDistVal();
                            break;
                        case 3:
                            // 随机提成
                            receiptorAmountOne = NumberUtils.getRandom(goodsDTO.getDistMinVal(), goodsDTO.getDistMaxVal());
                            break;
                        default:
                            break;
                    }
                }
                // 当前产品收益合计
                receiptorAmount = receiptorAmountOne.multiply(dto.getGoodsQty());
                // 订单收益合计
                dto.setBenefitPrice(receiptorAmount);
                dto.setBenefitStatus(1);
                orderItemService.updateDto(dto);
                // 收益大于零的情况下打开收益更新的开关
                if (receiptorAmount.doubleValue() > 0d) {
                    flag = true;
                }

                // 如果有收益变动，则对该用户进行付款操作
                if (flag) {
                    // 验证身份信息
                    //transfersService.orderIncomeTransfers(dto.getId());
                    benefitUser.setIncome(benefitUser.getIncome().add(receiptorAmount));
                    userService.updateDto(benefitUser);
                }

                GoodsEntity goods = goodsService.getById(dto.getGoodsId());
                goods.setSalesVolume(goods.getSalesVolume() != null ? goods.getSalesVolume() + 1 : 1);
                goodsService.updateById(goods);
            }
            if (receiptorAmountSum.doubleValue() > 0) {
                orderEntity.setBenefitPrice(receiptorAmountSum);
                orderEntity.setBenefitStatus(1);
                updateById(orderEntity);
            }

            System.out.println("完成收益分配，收益订单已处理，详情查看goods_item表格");
            return true;
        } catch (Exception e) {
            System.out.println("订单支付回调程序：分配收益程序错误!订单ID:" + orderId);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map> benefitRanking(Long goodsId) {
        List<Map> list = getBaseMapper().benefitRanking(goodsId);
        int i = 1;
        for (Map m : list) {
            UserEntity userEntity = userService.getById((Long) m.get("benefit_user_id"));
            m.put("userName", userEntity.getRealName());
            m.put("index", i);
            i++;
        }
        return list;
    }
}

package com.nb6868.onex.modules.aep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.pojo.Kv;
import com.nb6868.onex.booster.service.impl.CrudServiceImpl;
import com.nb6868.onex.booster.util.*;
import com.nb6868.onex.common.websocket.WebSocketServer;
import com.nb6868.onex.modules.aep.dao.SubscriptionPushMessageDao;
import com.nb6868.onex.modules.aep.dto.SubscriptionPushMessageDTO;
import com.nb6868.onex.modules.aep.entity.DeviceEntity;
import com.nb6868.onex.modules.aep.entity.EnterpriseUserEntity;
import com.nb6868.onex.modules.aep.entity.SubscriptionPushMessageEntity;
import com.nb6868.onex.modules.aep.service.DeviceService;
import com.nb6868.onex.modules.aep.service.EnterpriseService;
import com.nb6868.onex.modules.aep.service.EnterpriseUserService;
import com.nb6868.onex.modules.aep.service.SubscriptionPushMessageService;
import com.nb6868.onex.modules.msg.MsgConst;
import com.nb6868.onex.modules.msg.dto.MailSendRequest;
import com.nb6868.onex.modules.msg.service.MailLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AEP-订阅消息通知
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class SubscriptionPushMessageServiceImpl extends CrudServiceImpl<SubscriptionPushMessageDao, SubscriptionPushMessageEntity, SubscriptionPushMessageDTO> implements SubscriptionPushMessageService {

    @Autowired
    DeviceService deviceService;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EnterpriseUserService enterpriseUserService;
    @Autowired
    MailLogService mailLogService;

    @Override
    public QueryWrapper<SubscriptionPushMessageEntity> getWrapper(String method, Map<String, Object> params) {
        return new WrapperUtils<SubscriptionPushMessageEntity>(new QueryWrapper<>(), params)
                .eq("productId", "product_id")
                .eq("deviceId", "device_id")
                .eq("messageType", "message_type")
                // 创建时间区间
                .ge("startCreateTime", "create_time")
                .le("endCreateTime", "create_time")
                .and("payloadAppdataDataType", queryWrapper -> {
                    int payloadAppdataDataType = ParamUtils.toInt(params.get("payloadAppdataDataType"), -1);
                    if (payloadAppdataDataType == 0) {
                        // 正常
                        queryWrapper.eq("payload_appdata_data", "040100").or().eq("payload_appdata_data", "04010a");
                    } else if (payloadAppdataDataType == 1) {
                        // 漏电
                        queryWrapper.ne("payload_appdata_data", "040100").ne("payload_appdata_data", "04010a");
                    }
                })
                .apply(Const.SQL_FILTER)
                .getQueryWrapper();
    }

    @Autowired
    WebSocketServer webSocket;

    @Override
    public boolean notify(SubscriptionPushMessageDTO message) {
        // 保存消息
        SubscriptionPushMessageEntity entity = ConvertUtils.sourceToTarget(message, SubscriptionPushMessageEntity.class);
        entity.setPayload(JacksonUtils.pojoToJson(message.getPayload()));
        entity.setResult(JacksonUtils.pojoToJson(message.getResult()));
        entity.setEventContent(JacksonUtils.pojoToJson(message.getEventContent()));

        // 数据处理与存储、逻辑处理
        if (message.getMessageType().equalsIgnoreCase("deviceOnlineOfflineReport")) {
            // 上下线通知
            // 消息设备
            DeviceEntity device = deviceService.getByDeviceId(message.getDeviceId());
            if (device != null) {
                // 1. 修改设备网络状态
                deviceService.changeNetStatus(message.getDeviceId(), message.getEventType());
                // 2. 推送
                if (device.getAlarmPush() == 1) {
                    // 开启了推送
                    List<EnterpriseUserEntity> enterpriseUsers = enterpriseUserService.getListByEnterpriseId(device.getEnterpriseId());
                    if (ObjectUtils.isEmpty(enterpriseUsers)) {
                        List<Long> userIdList = new ArrayList<>();
                        List<String> userOpenidList = new ArrayList<>();
                        for (EnterpriseUserEntity enterpriseUser : enterpriseUsers) {
                            userIdList.add(enterpriseUser.getUserId());
                            if (StringUtils.isNotEmpty(enterpriseUser.getOpenid())) {
                                userOpenidList.add(enterpriseUser.getOpenid());
                            }
                        }
                        if (ObjectUtils.isNotEmpty(userIdList)) {
                            // socket通知
                            String leakType = message.getEventType() == 1 ? "上线" : "下线";
                            webSocket.sendMultiMessage(userIdList, JacksonUtils.pojoToJson(Kv.init()
                                    .set("type", "onOffAlarm")
                                    .set("message", "[" + device.getDeviceName() + "]" + leakType + "," + "请及时处理")
                                    .set("title", "设备" + leakType + "提醒")));
                        }
                        if (ObjectUtils.isNotEmpty(userOpenidList)) {
                            // 微信通知
                            String leakType = message.getEventType() == 1 ? "上线" : "下线";
                            MailSendRequest mailSendRequest = new MailSendRequest();
                            mailSendRequest.setTplType(MsgConst.MailTypeEnum.WX_MP_TEMPLATE.name());
                            mailSendRequest.setTplCode("ALARM_AEP_ONOFF");
                            mailSendRequest.setMailTo(org.apache.commons.lang3.StringUtils.join(userOpenidList, ","));
                            mailSendRequest.setContentParam(JacksonUtils.pojoToJson(Kv.init()
                                    .set("first", "设备" + leakType + "提醒")
                                    .set("keyword1", device.getDeviceName())
                                    .set("keyword2", leakType)
                                    .set("remark", "请关注," + DateUtils.format(new Date(entity.getTimestamp()), DateUtils.DATE_TIME_PATTERN))));
                            mailLogService.send(mailSendRequest);
                        }
                    }
                }
            }
        } else if (message.getMessageType().equalsIgnoreCase("dataReport")) {
            // 设备数据变化
            if (StringUtils.isNotEmpty(entity.getPayload())) {
                Map<String, Object> map = JacksonUtils.jsonToMap(entity.getPayload());
                if (map != null) {
                    Object appdata = map.get("APPdata");
                    if (!ObjectUtils.isEmpty(appdata)) {
                        List<String> payloadAppdata = StringUtils.byteToHexArray(Base64.getDecoder().decode(appdata.toString()));
                        entity.setPayloadAppdata(org.apache.commons.lang3.StringUtils.join(payloadAppdata, ""));
                        if (payloadAppdata.size() >= 8) {
                            String payloadAppdataDevice = payloadAppdata.get(1);
                            entity.setPayloadAppdataDevice(payloadAppdataDevice);
                            entity.setPayloadAppdataFun(payloadAppdata.get(2));
                            String payloadAppdataData = org.apache.commons.lang3.StringUtils.join(payloadAppdata.subList(6, payloadAppdata.size() - 2), "");
                            entity.setPayloadAppdataData(payloadAppdataData);
                            // 消息设备
                            DeviceEntity device = deviceService.getByDeviceId(message.getDeviceId());
                            if (device != null) {
                                // 1. 修改设备AEP状态
                                deviceService.changeAepStatus(message.getDeviceId(), payloadAppdataData);
                                // 2. 监控漏电
                                /**
                                 * 所有 040100 正常
                                 * A
                                 * 040111 零线漏电
                                 * 040101 火线漏电
                                 *
                                 * B
                                 * 04010C  零线漏电
                                 * 040104  火线漏电
                                 *
                                 * C
                                 * 040110 火线漏电
                                 * 040130 零线漏电
                                 *
                                 * 三相
                                 * ？
                                 *
                                 * 开关
                                 * 04010a 正常
                                 * 04010b 漏电
                                 */
                                if (!payloadAppdataData.equalsIgnoreCase("040100") && !payloadAppdataData.equalsIgnoreCase("04010a")) {
                                    // 漏电了
                                    String deviceType = "设备";
                                    if (payloadAppdataDevice.equalsIgnoreCase("00")) {
                                        deviceType = "开关设备";
                                    } else if (payloadAppdataDevice.equalsIgnoreCase("ff")) {
                                        deviceType = "三相设备";
                                    } else if (payloadAppdataDevice.equalsIgnoreCase("01")) {
                                        deviceType = "单相A设备";
                                    } else if (payloadAppdataDevice.equalsIgnoreCase("02")) {
                                        deviceType = "单相B设备";
                                    } else if (payloadAppdataDevice.equalsIgnoreCase("03")) {
                                        deviceType = "单相C设备";
                                    }
                                    String leakType;
                                    if (entity.getPayloadAppdataData().equalsIgnoreCase("040111")
                                            || entity.getPayloadAppdataData().equalsIgnoreCase("04010C")
                                            || entity.getPayloadAppdataData().equalsIgnoreCase("040130")
                                            || entity.getPayloadAppdataData().equalsIgnoreCase("04013f")) {
                                        leakType = "零线漏电";
                                    } else if (entity.getPayloadAppdataData().equalsIgnoreCase("040101") || entity.getPayloadAppdataData().equalsIgnoreCase("040104") || entity.getPayloadAppdataData().equalsIgnoreCase("040110")) {
                                        leakType = "火线漏电";
                                    } else if (entity.getPayloadAppdataData().equalsIgnoreCase("04010b")) {
                                        leakType = "开关漏电";
                                    } else {
                                        leakType = "漏电" + entity.getPayloadAppdataData();
                                    }

                                    // 2. 推送
                                    if (device.getAlarmPush() == 1) {
                                        // 开启了推送
                                        List<EnterpriseUserEntity> enterpriseUsers = enterpriseUserService.getListByEnterpriseId(device.getEnterpriseId());
                                        if (ObjectUtils.isNotEmpty(enterpriseUsers)) {
                                            List<Long> userIdList = new ArrayList<>();
                                            List<String> userOpenidList = new ArrayList<>();
                                            for (EnterpriseUserEntity enterpriseUser : enterpriseUsers) {
                                                userIdList.add(enterpriseUser.getUserId());
                                                if (StringUtils.isNotEmpty(enterpriseUser.getOpenid())) {
                                                    userOpenidList.add(enterpriseUser.getOpenid());
                                                }
                                            }
                                            if (ObjectUtils.isNotEmpty(userIdList)) {
                                                // socket通知
                                                webSocket.sendMultiMessage(userIdList, JacksonUtils.pojoToJson(Kv.init()
                                                        .set("type", "leakAlarm")
                                                        .set("message", "[" + device.getDeviceName() + "]" + leakType + "," + "请及时处理")
                                                        .set("title",  deviceType + "漏电提醒")));
                                            }
                                            if (ObjectUtils.isNotEmpty(userOpenidList)) {
                                                // 微信通知
                                                MailSendRequest mailSendRequest = new MailSendRequest();
                                                mailSendRequest.setTplType(MsgConst.MailTypeEnum.WX_MP_TEMPLATE.name());
                                                mailSendRequest.setTplCode("ALARM_AEP_LEAK");
                                                mailSendRequest.setMailTo(org.apache.commons.lang3.StringUtils.join(userOpenidList, ","));
                                                mailSendRequest.setContentParam(JacksonUtils.pojoToJson(Kv.init()
                                                        .set("first", "设备漏电提醒")
                                                        .set("keyword1", device != null ? device.getDeviceName() : "未知设备")
                                                        .set("keyword2", leakType)
                                                        .set("keyword3", DateUtils.format(new Date(entity.getTimestamp()), DateUtils.DATE_TIME_PATTERN))
                                                        .set("keyword4", deviceType + "漏电,请及时处理")
                                                        .set("remark", "请及时处理")));
                                                mailLogService.send(mailSendRequest);
                                            }
                                        }
                                    }
                                    // todo 漏电逻辑处理
                                }
                            }
                        }
                    }
                }
            }
        } else if (message.getMessageType().equalsIgnoreCase("commandResponse")) {
            // 设备命令响应
        } else if (message.getMessageType().equalsIgnoreCase("eventReport")) {
            // 设备事件上报
        }
        save(entity);
        return true;
    }

}

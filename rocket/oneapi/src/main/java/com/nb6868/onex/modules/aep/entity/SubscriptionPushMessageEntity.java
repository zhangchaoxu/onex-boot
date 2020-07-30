package com.nb6868.onex.modules.aep.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * AEP-订阅消息通知
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("aep_subscription_push_message")
@Alias("aep_subscription_push_message")
public class SubscriptionPushMessageEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
	private Long productId;
    /**
     * 设备ID
     */
	private String deviceId;
    /**
     * 消息类型
     */
	private String messageType;
    /**
     * imei
     */
	private String imei;
    /**
     * imsi
     */
	private String imsi;
    /**
     * 时间戳
     */
	private Long timestamp;
    /**
     * 设备标识
     */
	private String deviceType;
    /**
     * 数据上报主题
     */
	private String topic;
    /**
     * 合作伙伴ID
     */
	private String assocAssetId;
    /**
     * 上行报文序号
     */
	private Integer upPacketSn;
    /**
     * 数据上报报文序号
     */
	private Integer upDataSn;
    /**
     * 服务ID
     */
	private String serviceId;
    /**
     * 协议
     */
	private String protocol;
    /**
     * 消息荷载
     */
	private String payload;
	/**
	 * 消息荷载
	 */
	private String payloadAppdata;

	/**
	 * 消息荷载(数据域)
	 */
	private String payloadAppdataData;
	/**
	 * 消息荷载(功能码)
	 */
	private String payloadAppdataFun;
	/**
	 * 消息荷载(设备类型)
	 */
	private String payloadAppdataDevice;
	/**
     * 指令任务ID
     */
	private String taskId;
    /**
     * 指令执行结果
     */
	private String result;
    /**
     * 设备编号
     */
	private String deviceSn;
    /**
     * 事件类型
     */
	private Integer eventType;
    /**
     * 事件上报数据
     */
	private String eventContent;
    /**
     * 租户id
     */
	private Long tenantId;
    /**
     * 租户名称
     */
	private String tenantName;

}

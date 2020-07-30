package com.nb6868.onex.modules.aep.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AEP-订阅消息通知
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "AEP-订阅消息通知")
public class SubscriptionPushMessageDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "产品ID")
	private Long productId;

	@ApiModelProperty(value = "设备ID")
	private String deviceId;

	/**
	 * 设备数据变化 dataReport
	 * 设备命令响应 commandResponse
	 * 设备事件上报 eventReport
	 * 设备上下线 deviceOnlineOfflineReport
	 */
	@ApiModelProperty(value = "消息类型")
	private String messageType;

	@ApiModelProperty(value = "imei")
	private String imei;

	@ApiModelProperty(value = "imsi")
	private String imsi;

	@ApiModelProperty(value = "时间戳")
	private Long timestamp;

	@ApiModelProperty(value = "设备标识")
	private String deviceType;

	@ApiModelProperty(value = "数据上报主题")
	private String topic;

	@ApiModelProperty(value = "合作伙伴ID")
	private String assocAssetId;

	@ApiModelProperty(value = "上行报文序号")
	private Integer upPacketSn;

	@ApiModelProperty(value = "数据上报报文序号")
	private Integer upDataSn;

	@ApiModelProperty(value = "服务ID")
	private String serviceId;

	@ApiModelProperty(value = "协议")
	private String protocol;

	@ApiModelProperty(value = "消息荷载")
	private Object payload;

	@ApiModelProperty(value = "消息荷载Appdata")
	private String payloadAppdata;

	@ApiModelProperty(value = "消息荷载(数据域)")
	private String payloadAppdataData;

	@ApiModelProperty(value = "消息荷载(功能码)")
	private String payloadAppdataFun;

	@ApiModelProperty(value = "消息荷载(设备类型)")
	private String payloadAppdataDevice;

	@ApiModelProperty(value = "指令任务ID")
	private String taskId;

	@ApiModelProperty(value = "指令执行结果")
	private Object result;

	@ApiModelProperty(value = "设备编号")
	private String deviceSn;

	@ApiModelProperty(value = "事件类型")
	private Integer eventType;

	@ApiModelProperty(value = "事件上报数据")
	private Object eventContent;

	@ApiModelProperty(value = "租户id")
	private Long tenantId;

	@ApiModelProperty(value = "租户名称")
	private String tenantName;

}

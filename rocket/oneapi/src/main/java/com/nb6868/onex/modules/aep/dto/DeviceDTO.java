package com.nb6868.onex.modules.aep.dto;

import com.nb6868.onex.booster.pojo.BaseTenantDTO;
import com.nb6868.onex.booster.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * AEP-设备
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "AEP-设备")
public class DeviceDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "产品ID")
	@NotNull(message = "产品ID不能为空", groups = DefaultGroup.class)
	private Integer productId;

	@ApiModelProperty(value = "设备ID")
	private String deviceId;

	@ApiModelProperty(value = "设备名称")
	private String deviceName;

	@ApiModelProperty(value = "上级设备ID")
	@NotBlank(message = "产品ID不能为空", groups = DefaultGroup.class)
	private String devicePid;

	@ApiModelProperty(value = "设备类型")
	private String deviceType;

	@ApiModelProperty(value = "防漏电状态")
	private String aepStatus;

	@ApiModelProperty(value = "imei")
	private String imei;

	@ApiModelProperty(value = "imsi")
	private String imsi;

	@ApiModelProperty(value = "状态")
	private Integer deviceStatus;

	@ApiModelProperty(value = "自动订阅")
	private Integer autoObserver;

	@ApiModelProperty(value = "网络状态")
	private Integer netStatus;

	@ApiModelProperty(value = "最后上线时间")
	private Long onlineAt;

	@ApiModelProperty(value = "最后离线时间")
	private Long offlineAt;

	@ApiModelProperty(value = "固件版本")
	private String firmwareVersion;

	@ApiModelProperty(value = "是否推送报警")
	private Integer alarmPush;

	@ApiModelProperty(value = "漏电处理规则")
	private String interruptRule;

	@ApiModelProperty(value = "企业id")
	@NotNull(message = "企业ID不能为空", groups = DefaultGroup.class)
	private Long enterpriseId;

	@ApiModelProperty(value = "企业名称")
	private String enterpriseName;
}

package com.nb6868.onex.modules.aep.dto;

import com.nb6868.onex.booster.pojo.BaseTenantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AEP-产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "AEP-产品")
public class ProductDTO extends BaseTenantDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "MasterKey")
	private String apiKey;

	@ApiModelProperty(value = "产品ID")
	private Integer productId;

	@ApiModelProperty(value = "产品名称")
	private String productName;

	@ApiModelProperty(value = "产品类别")
	private Integer productType;

	@ApiModelProperty(value = "产品分类名称")
	private String productTypeValue;

	@ApiModelProperty(value = "二级分类")
	private Integer secondaryType;

	@ApiModelProperty(value = "二级分类名称")
	private String secondaryTypeValue;

	@ApiModelProperty(value = "三级分类")
	private Integer thirdType;

	@ApiModelProperty(value = "三级分类名称")
	private String thirdTypeValue;

	@ApiModelProperty(value = "产品协议：1.T-LINK协议 2.MQTT协议 3.LWM2M协议 4.TUP协议 5.HTTP协议 6.JT/T808 7.TCP协议 8.私有TCP(网关子设备协议) 9.私有UDP(网关子设备协议) 10.网关产品MQTT(网关产品协议) 11.南向云")
	private Integer productProtocol;

	@ApiModelProperty(value = "网络类型:1.wifi2.移动蜂窝数据3.NB-IoT4.以太网")
	private Integer networkType;

	@ApiModelProperty(value = "认证方式 1:特征串认证，2:sm9认证，3:dtls双向认证,4:IMEI认证,5:SIMID认证，6:SM2认证")
	private Integer authType;

	@ApiModelProperty(value = "设备总数")
	private Long deviceCount;

	@ApiModelProperty(value = "在线设备数")
	private Long onlineDeviceCount;

	@ApiModelProperty(value = "省电模式：1.PSM 2.DRX 3.eDRX")
	private Integer powerModel;

	@ApiModelProperty(value = "设备接入IP端口")
	private String deviceIpPort;

	@ApiModelProperty(value = "完整信息")
	private String ext;

}

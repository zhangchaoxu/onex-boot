package com.nb6868.onex.modules.aep.sdk;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductResponse implements Serializable {

    /**
     * MasterKey
     */
    private String apiKey;
    /**
     * 产品ID
     */
    private Integer productId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品类别
     */
    private Integer productType;
    /**
     * 产品分类名称
     */
    private String productTypeValue;
    /**
     * 二级分类
     */
    private Integer secondaryType;
    /**
     * 二级分类名称
     */
    private String secondaryTypeValue;
    /**
     * 三级分类
     */
    private Integer thirdType;
    /**
     * 三级分类名称
     */
    private String thirdTypeValue;
    /**
     * 产品协议：1.T-LINK协议 2.MQTT协议 3.LWM2M协议 4.TUP协议 5.HTTP协议 6.JT/T808 7.TCP协议 8.私有TCP(网关子设备协议) 9.私有UDP(网关子设备协议) 10.网关产品MQTT(网关产品协议) 11.南向云
     */
    private Integer productProtocol;
    /**
     * 网络类型:1.wifi2.移动蜂窝数据3.NB-IoT4.以太网
     */
    private Integer networkType;
    /**
     * 认证方式 1:特征串认证，2:sm9认证，3:dtls双向认证,4:IMEI认证,5:SIMID认证，6:SM2认证
     */
    private Integer authType;
    /**
     * 设备总数
     */
    private Long deviceCount;
    /**
     * 在线设备数
     */
    private Long onlineDeviceCount;
    /**
     * 省电模式：1.PSM 2.DRX 3.eDRX
     */
    private Integer powerModel;
    /**
     * 设备接入IP端口
     */
    private String deviceIpPort;

}

package com.nb6868.onex.modules.aep.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * AEP-产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "MasterKey")
    private String apiKey;
    @Excel(name = "产品ID")
    private Integer productId;
    @Excel(name = "产品名称")
    private String productName;
    @Excel(name = "产品类别")
    private Integer productType;
    @Excel(name = "产品分类名称")
    private String productTypeValue;
    @Excel(name = "二级分类")
    private Integer secondaryType;
    @Excel(name = "二级分类名称")
    private String secondaryTypeValue;
    @Excel(name = "三级分类")
    private Integer thirdType;
    @Excel(name = "三级分类名称")
    private String thirdTypeValue;
    @Excel(name = "产品协议：1.T-LINK协议 2.MQTT协议 3.LWM2M协议 4.TUP协议 5.HTTP协议 6.JT/T808 7.TCP协议 8.私有TCP(网关子设备协议) 9.私有UDP(网关子设备协议) 10.网关产品MQTT(网关产品协议) 11.南向云")
    private Integer productProtocol;
    @Excel(name = "网络类型:1.wifi2.移动蜂窝数据3.NB-IoT4.以太网")
    private Integer networkType;
    @Excel(name = "认证方式 1:特征串认证，2:sm9认证，3:dtls双向认证,4:IMEI认证,5:SIMID认证，6:SM2认证")
    private Integer authType;
    @Excel(name = "设备总数")
    private Long deviceCount;
    @Excel(name = "在线设备数")
    private Long onlineDeviceCount;
    @Excel(name = "省电模式：1.PSM 2.DRX 3.eDRX")
    private Integer powerModel;
    @Excel(name = "设备接入IP端口")
    private String devicdeIpPort;
    @Excel(name = "完整信息")
    private String ext;
    @Excel(name = "创建者id")
    private Long createId;
    @Excel(name = "创建者名字")
    private String createName;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "更新者id")
    private Long updateId;
    @Excel(name = "更新时间")
    private Date updateTime;
    @Excel(name = "删除标记,1已删除 0 未删除")
    private Integer deleted;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;

}

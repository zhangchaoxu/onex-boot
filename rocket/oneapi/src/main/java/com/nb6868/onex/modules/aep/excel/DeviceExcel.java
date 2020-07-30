package com.nb6868.onex.modules.aep.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * AEP-设备
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeviceExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "产品ID")
    private Integer productId;
    @Excel(name = "设备ID")
    private String deviceId;
    @Excel(name = "设备名称")
    private String deviceName;
    @Excel(name = "imei")
    private String imei;
    @Excel(name = "imsi")
    private String imsi;
    @Excel(name = "状态")
    private Integer deviceStatus;
    @Excel(name = "")
    private Integer autoObserver;
    @Excel(name = "")
    private Integer netStatus;
    @Excel(name = "")
    private Long onlineAt;
    @Excel(name = "")
    private Long offlineAt;
    @Excel(name = "")
    private String firmwareVersion;
    @Excel(name = "创建者id")
    private Long createId;
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

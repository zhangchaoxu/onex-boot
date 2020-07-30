package com.nb6868.onex.modules.aep.sdk;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceResponse implements Serializable {

    /**
     * 产品ID
     */
    private Integer productId;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * imei
     */
    private String imei;
    /**
     * imsi
     */
    private String imsi;
    /**
     * 状态
     */
    private Integer deviceStatus;
    /**
     *
     */
    private Integer autoObserver;
    /**
     *
     */
    private Integer netStatus;
    /**
     *
     */
    private Long onlineAt;
    /**
     *
     */
    private Long offlineAt;
    /**
     *
     */
    private String firmwareVersion;

}

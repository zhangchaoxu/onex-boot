package com.nb6868.onex.modules.aep.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * AEP-设备
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("aep_device")
@Alias("aep_device")
public class DeviceEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
	private Integer productId;
    /**
     * 设备ID
     */
	private String deviceId;
	/**
	 * 上级设备ID
	 */
	private String devicePid;
	/**
	 * 设备类型
	 */
	private String deviceType;
	/**
	 * 防漏电状态
	 */
	private String aepStatus;
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

	/**
	 * 是否推送报警
	 */
	private Integer alarmPush;
	/**
	 * 漏电处理规则
	 */
	private String interruptRule;
	/**
	 * 企业id
	 */
	private Long enterpriseId;
	/**
	 * 企业名称
	 */
	private String enterpriseName;

}

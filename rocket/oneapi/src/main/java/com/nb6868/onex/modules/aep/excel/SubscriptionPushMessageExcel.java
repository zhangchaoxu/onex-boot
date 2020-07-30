package com.nb6868.onex.modules.aep.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * AEP-订阅消息通知
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SubscriptionPushMessageExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "产品ID")
    private Long productId;
    @Excel(name = "设备ID")
    private String deviceId;
    @Excel(name = "消息类型")
    private String messageType;
    @Excel(name = "imei")
    private String imei;
    @Excel(name = "imsi")
    private String imsi;
    @Excel(name = "时间戳")
    private Long timestamp;
    @Excel(name = "设备标识")
    private String deviceType;
    @Excel(name = "数据上报主题")
    private String topic;
    @Excel(name = "合作伙伴ID")
    private String assocAssetId;
    @Excel(name = "上行报文序号")
    private Integer upPacketSn;
    @Excel(name = "数据上报报文序号")
    private Integer upDataSn;
    @Excel(name = "服务ID")
    private String serviceId;
    @Excel(name = "协议")
    private String protocol;
    @Excel(name = "消息荷载")
    private String payload;
    @Excel(name = "指令任务ID")
    private String taskId;
    @Excel(name = "指令执行结果")
    private String result;
    @Excel(name = "设备编号")
    private String deviceSn;
    @Excel(name = "事件类型")
    private Integer eventType;
    @Excel(name = "事件上报数据")
    private String eventContent;
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

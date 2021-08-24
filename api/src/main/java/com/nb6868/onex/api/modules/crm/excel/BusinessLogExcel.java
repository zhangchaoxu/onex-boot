package com.nb6868.onex.api.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * CRM商机记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessLogExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "创建者")
    private Long createId;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "更新者")
    private Long updateId;
    @Excel(name = "更新时间")
    private Date updateTime;
    @Excel(name = "删除标记")
    private Integer deleted;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;
    @Excel(name = "客户id")
    private Long customerId;
    @Excel(name = "商机id")
    private Long businessId;
    @Excel(name = "记录类型 followup跟进 new创建 edit修改 close关闭")
    private String type;
    @Excel(name = "记录时间，比如跟进时间")
    private String logDate;
    @Excel(name = "内容")
    private String content;
    @Excel(name = "内容附件")
    private String contentAttachment;

}

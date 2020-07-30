package com.nb6868.onex.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CRM商机
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessExcel {
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
    @Excel(name = "客户id")
    private Long customerId;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;
    @Excel(name = "商机来源")
    private String source;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "跟进时间")
    private Date followDate;
    @Excel(name = "状态1 阶段1 2 阶段2 3 阶段3 10 赢单 -10 输单 0 无效")
    private Integer status;
    @Excel(name = "商机金额")
    private BigDecimal amont;
    @Excel(name = "预计成交时间")
    private Date dealDate;
    @Excel(name = "产品折扣")
    private BigDecimal productDiscount;
    @Excel(name = "产品总价")
    private BigDecimal productPrice;
    @Excel(name = "关联销售id")
    private Long salesUserId;

}

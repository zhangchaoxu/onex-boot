package com.nb6868.onexboot.api.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CRM合同
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ContractExcel {
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
    @Excel(name = "支付方式")
    private String paytype;
    @Excel(name = "合同内容（存放html形式）")
    private String content;
    @Excel(name = "合同附件")
    private String contentAttachment;
    @Excel(name = "合同签订日期")
    private Date contractDate;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;
    @Excel(name = "合同名称")
    private String name;
    @Excel(name = "合同编号")
    private String code;
    @Excel(name = "商机id")
    private Long businessId;
    @Excel(name = "合同金额")
    private BigDecimal amount;
    @Excel(name = "下单日期")
    private Date orderDate;
    @Excel(name = "合同开始日期")
    private Date validStartDate;
    @Excel(name = "合同结束日期")
    private Date validEndDate;
    @Excel(name = "客户签约人")
    private String customerSigner;
    @Excel(name = "公司签约人")
    private String supplierSigner;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "产品折扣")
    private BigDecimal productDiscount;
    @Excel(name = "产品总价")
    private BigDecimal productPrice;
    @Excel(name = "关联销售id")
    private Long salesUserId;
    @Excel(name = "销售提成")
    private BigDecimal salesPercentageAmont;

}

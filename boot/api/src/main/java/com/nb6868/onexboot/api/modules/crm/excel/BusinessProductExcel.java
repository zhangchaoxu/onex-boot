package com.nb6868.onexboot.api.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CRM商机-产品明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessProductExcel {
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
    @Excel(name = "商机id")
    private Long businessId;
    @Excel(name = "租户id")
    private Long tenantId;
    @Excel(name = "租户名称")
    private String tenantName;
    @Excel(name = "产品id")
    private Long productId;
    @Excel(name = "产品单位")
    private String productUnit;
    @Excel(name = "产品分类id")
    private Long productCategoryId;
    @Excel(name = "产品分类名称")
    private String productCategoryName;
    @Excel(name = "数量")
    private BigDecimal qty;
    @Excel(name = "产品标准价格")
    private BigDecimal salePrice;
    @Excel(name = "折扣")
    private BigDecimal discount;
    @Excel(name = "产品折扣价格")
    private BigDecimal discountPrice;
    @Excel(name = "价格小计")
    private BigDecimal totalPrice;

}

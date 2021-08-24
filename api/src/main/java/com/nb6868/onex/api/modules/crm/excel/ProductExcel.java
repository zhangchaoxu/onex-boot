package com.nb6868.onex.api.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * CRM产品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductExcel {
    @Excel(name = "产品名称")
    private String name;
    @Excel(name = "产品编码")
    private String sn;
    @Excel(name = "单位")
    private String unit;
    @Excel(name = "售价")
    private BigDecimal salePrice;
    @Excel(name = "图文内容")
    private String content;
    @Excel(name = "产品分类")
    private String categoryName;
    @Excel(name = "是否上架", replace = {"是_1", "否_0"})
    private Integer marketable;
    @Excel(name = "租户名称")
    private String tenantName;

}

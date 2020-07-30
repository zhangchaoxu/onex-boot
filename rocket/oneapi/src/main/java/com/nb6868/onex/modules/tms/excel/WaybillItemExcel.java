package com.nb6868.onex.modules.tms.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * TMS-运单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaybillItemExcel {

    @Excel(name = "运单号")
    private String waybillCode;
    @Excel(name = "进货日期", format = "yyyy-MM-dd")
    private String purchaseDate;
    @Excel(name = "供应商")
    private String supplierName;
    @Excel(name = "箱号")
    private String code;
    @Excel(name = "封号")
    private String sealCode;
    @Excel(name = "品种")
    private String goodsType;
    @Excel(name = "货名")
    private String goods;
    @Excel(name = "数量")
    private BigDecimal qty;
    @Excel(name = "单位")
    private String unit;
    @Excel(name = "卸货数量")
    private BigDecimal qtyUnload;
    @Excel(name = "单价")
    private BigDecimal price;
    @Excel(name = "备注")
    private String remark;

}

package com.nb6868.onex.modules.tms.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TMS-运单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WaybillExcel {

    @Excel(name = "运单号")
    private String code;
    @Excel(name = "发货人")
    private String sender;
    @Excel(name = "船公司")
    private String carrierOwner;
    @Excel(name = "船号")
    private String carrierName;
    @Excel(name = "船次")
    private String carrierNo;
    @Excel(name = "离港日", format = "yyyy-MM-dd")
    private Date carrierFromDate;
    @Excel(name = "装货港")
    private String carrierFrom;
    @Excel(name = "卸货港")
    private String carrierTo;
    @Excel(name = "到港日", format = "yyyy-MM-dd")
    private Date carrierToDate;
    @Excel(name = "运费")
    private BigDecimal priceTotal;
    @Excel(name = "备注")
    private String remark;

}

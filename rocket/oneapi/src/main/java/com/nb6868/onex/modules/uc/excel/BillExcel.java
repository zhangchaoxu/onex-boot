package com.nb6868.onex.modules.uc.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账单流水
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BillExcel {

    @Excel(name = "用户姓名")
    private String userName;
    @Excel(name = "操作类型")
    private Integer optType;
    @Excel(name = "类型", replace = {"账户_balance", "收入_income", "积分_points"})
    private String type;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "金额")
    private BigDecimal amount;
    @Excel(name = "账户余额")
    private BigDecimal balance;
    @Excel(name = "收入余额")
    private BigDecimal income;
    @Excel(name = "积分余额")
    private BigDecimal points;
    @Excel(name = "相关订单")
    private Long orderId;
    @Excel(name = "操作时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}

package com.nb6868.onex.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SupplierExcel {
    @Excel(name = "编码")
    private String code;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "备注")
    private String remark;
    @Excel(name = "图片")
    private String imgs;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "状态", replace = {"未审核_0", "已审核_1"})
    private Integer status;
    @Excel(name = "简介")
    private String content;

}

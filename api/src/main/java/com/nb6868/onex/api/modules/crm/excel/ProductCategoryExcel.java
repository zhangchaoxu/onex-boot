package com.nb6868.onex.api.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM 产品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductCategoryExcel {

    @Excel(name = "租户名")
    private String tenantName;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "描述")
    private String content;

}

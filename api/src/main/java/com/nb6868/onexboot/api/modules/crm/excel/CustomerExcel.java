package com.nb6868.onexboot.api.modules.crm.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CRM客户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerExcel {

    @Excel(name = "名称")
    private String name;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "级别", replace = {"重点_1", "普通_2", "非优先_3"})
    private Integer level;
    @Excel(name = "客户来源")
    private String source;
    @Excel(name = "成交状态", replace = {"未成交_0", "已成交_1"})
    private Integer dealStatus;
    @Excel(name = "联系人")
    private String contacts;
    @Excel(name = "联系电话")
    private String telephone;
    @Excel(name = "联系手机")
    private String mobile;
    @Excel(name = "内容")
    private String content;
    @Excel(name = "状态", replace = {"未激活_0", "激活_1"})
    private Integer status;
    @Excel(name = "区域")
    private String regionName;
    @Excel(name = "区域编码")
    private String regionCode;
    @Excel(name = "详细地址")
    private String address;
    @Excel(name = "备注")
    private String remark;

}

package com.nb6868.onexboot.api.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 商品类别
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "父id")
    private Long pid;
    @Excel(name = "店铺编码")
    private String storeCode;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "logo")
    private String logo;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "介绍")
    private String content;
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

}

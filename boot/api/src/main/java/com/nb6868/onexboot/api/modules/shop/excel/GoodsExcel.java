package com.nb6868.onexboot.api.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsExcel {
    @Excel(name = "品牌id")
    private Long brandId;
    @Excel(name = "分类id")
    private Long categoryId;
    @Excel(name = "供应商id")
    private Long supplierId;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "编号")
    private String sn;
    @Excel(name = "是否需要物流")
    private Integer delivery;
    @Excel(name = "是否上架")
    private Integer marketable;
    @Excel(name = "是否置顶")
    private Integer top;
    @Excel(name = "类型 1 商品 2 积分兑换 3 赠品")
    private Integer type;
    @Excel(name = "名称")
    private String name;
    @Excel(name = "标题")
    private String title;
    @Excel(name = "标签,逗号分隔")
    private String tags;
    @Excel(name = "市场价")
    private BigDecimal marketPrice;
    @Excel(name = "售价")
    private BigDecimal salePrice;
    @Excel(name = "属性,不会影响价格、数量等业务")
    private String attrs;
    @Excel(name = "规格")
    private String specs;
    @Excel(name = "状态")
    private Integer status;
    @Excel(name = "点击数")
    private Integer hits;
    @Excel(name = "图片")
    private String imgs;
    @Excel(name = "图文内容")
    private String content;
    @Excel(name = "评分")
    private Float score;

}

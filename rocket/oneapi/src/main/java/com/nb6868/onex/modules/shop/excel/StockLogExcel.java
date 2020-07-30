package com.nb6868.onex.modules.shop.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 出入库记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StockLogExcel {

    @Excel(name = "商品id")
    private Long goodsId;
    @Excel(name = "类型", replace = {"入库_0", "出库_1"})
    private Integer type;
    @Excel(name = "入库数量")
    private Integer inQty;
    @Excel(name = "出库数量")
    private Integer outQty;
    @Excel(name = "出入库后库存")
    private Integer stock;
    @Excel(name = "操作人")
    private Long createId;
    @Excel(name = "操作时间")
    private Date createTime;

}

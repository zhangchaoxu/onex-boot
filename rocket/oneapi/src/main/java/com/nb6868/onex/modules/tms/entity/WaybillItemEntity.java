package com.nb6868.onex.modules.tms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * TMS-运单明细
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tms_waybill_item")
@Alias("tms_waybill_item")
public class WaybillItemEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 供应商id
     */
    private Long supplierId;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 箱号
     */
    private String code;
    /**
     * 封号
     */
    private String sealCode;
    /**
     * 货名
     */
    private String goods;
    /**
     * 品种
     */
    private String goodsType;
    /**
     * 单位
     */
    private String unit;
    /**
     * 数量
     */
    private BigDecimal qty;
    /**
     * 卸货数量
     */
    private BigDecimal qtyUnload;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建者名字
     */
    private String createName;
    /**
     * 运单id
     */
    private Long waybillId;
    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 进货时间
     */
    private String purchaseDate;

}

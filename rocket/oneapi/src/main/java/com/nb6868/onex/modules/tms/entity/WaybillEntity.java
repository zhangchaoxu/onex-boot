package com.nb6868.onex.modules.tms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TMS-运单
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tms_waybill")
@Alias("tms_waybill")
public class WaybillEntity extends BaseTenantEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String code;
    /**
     * 发货人
     */
    private String sender;
    /**
     * 运输载体主人
     */
    private String carrierOwner;
    /**
     * 运输载体名称
     */
    private String carrierName;
    /**
     * 运输载体次号
     */
    private String carrierNo;
    /**
     * 出发日期
     */
    private Date carrierFromDate;
    /**
     * 出发地
     */
    private String carrierFrom;
    /**
     * 目的地
     */
    private String carrierTo;
    /**
     * 到达日期
     */
    private Date carrierToDate;
    /**
     * 运输收费类型, 1 包邮、2 明细
     */
    private Integer priceType;
    /**
     * 总价
     */
    private BigDecimal priceTotal;

    private BigDecimal price1;
    private BigDecimal price2;
    private BigDecimal price3;
    private BigDecimal price4;
    private BigDecimal price5;
    private BigDecimal price6;
    private BigDecimal price7;
    private BigDecimal price8;
    private BigDecimal price9;
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
     * 设备数量
     */
    @TableField(exist = false)
    private Integer waybillItemCount;
}

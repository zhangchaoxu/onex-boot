package com.nb6868.onex.api.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 出入库记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_stock_log")
public class StockLogEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 创建人姓名
	 */
	@TableField(fill = FieldFill.INSERT)
	private String createName;
	/**
	 * 商品id
	 */
	private Long goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * remark
	 */
	private String remark;
	/**
	 * 类型 0 入库 1 出库
	 */
	private Integer type;
	/**
	 * 入库数量
	 */
	private BigDecimal inQty;
	/**
	 * 出库数量
	 */
	private BigDecimal outQty;
	/**
	 * 出入库后库存
	 */
	private BigDecimal stock;
}

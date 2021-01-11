package com.nb6868.onexboot.api.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户等级
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_user_rank")
public class UserRankEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
	private String name;
    /**
     * 消费金额
     */
	private BigDecimal amount;
    /**
     * 默认项
     */
	private Integer defaultItem;
	/**
	 * 排序
	 */
	private Integer sort;
    /**
     * 是否特殊
     */
	private Integer special;
    /**
     * 优惠比例0-1
     */
	private Double scale;
    /**
     * 状态  0：停用   1：正常
     */
	private Integer status;
}

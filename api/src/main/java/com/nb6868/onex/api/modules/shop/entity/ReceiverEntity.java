package com.nb6868.onex.api.modules.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收件地址
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("shop_receiver")
public class ReceiverEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
	private Long userId;
	/**
	 * 标签
	 */
	private String tag;
	/**
	 * 纬度
	 */
	private Double lat;
	/**
	 * 经度
	 */
	private Double lng;
    /**
     * 区域名称,如浙江省,宁波市,鄞州区
     */
	private String regionName;
    /**
     * 区域编号,如33000,33010,33011
     */
	private String regionCode;
    /**
     * 详细门牌号
     */
	private String address;
    /**
     * 收件人
     */
	private String consignee;
    /**
     * 邮编
     */
	private String zipCode;
    /**
     * 收件人手机号
     */
	private String mobile;
    /**
     * 默认项
     */
	private Integer defaultItem;

	/**
	 * 用户名
	 */
	private String userName;
}

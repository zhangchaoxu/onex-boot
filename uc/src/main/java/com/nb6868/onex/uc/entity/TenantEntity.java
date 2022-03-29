package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_tenant")
public class TenantEntity extends BaseEntity {

	/**
	 * 编码
	 */
	private String code;
    /**
     * 名称
     */
	private String name;
    /**
     * 状态 0 无效 1 有效
     */
	private Integer state;
}

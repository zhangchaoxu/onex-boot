package com.nb6868.onexboot.api.modules.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onexboot.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 租户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_tenant")
public class TenantEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
	private String name;
    /**
     * 有效期开始
     */
	private Date validStartTime;
    /**
     * 有效期结束
     */
	private Date validEndTime;
    /**
     * 状态 0 无效 1 有效
     */
	private Integer status;
}

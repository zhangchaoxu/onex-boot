package com.nb6868.onex.modules.aep.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.booster.pojo.BaseTenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * AEP-企业用户关联关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("aep_enterprise_user")
@Alias("aep_enterprise_user")
public class EnterpriseUserEntity extends BaseTenantEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 企业id
     */
	private Long enterpriseId;
    /**
     * 用户id
     */
	private Long userId;
	/**
	 * 微信openid
	 */
	@TableField(exist = false)
	private String openid;

}

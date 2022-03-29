package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_user_param")
@Alias("uc_user_param")
public class UserParamEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
	private String code;
    /**
     * 用户ID
     */
	private Long userId;
    /**
     * 参数
     */
	private String content;
    /**
     * 租户编码
     */
	private String tenantCode;

}

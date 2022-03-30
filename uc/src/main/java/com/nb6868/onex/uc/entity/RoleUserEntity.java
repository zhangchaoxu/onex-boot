package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 角色-用户关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_role_user")
@Alias("uc_role_user")
public class RoleUserEntity extends BaseEntity {

    /**
     * 角色编码
     */
	private String roleCode;
    /**
     * 用户ID
     */
	private Long userId;
}

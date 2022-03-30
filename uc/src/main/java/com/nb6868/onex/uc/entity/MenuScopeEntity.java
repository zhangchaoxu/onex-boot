package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 角色(用户)-菜单权限关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_menu_scope")
@Alias("uc_menu_scope")
public class MenuScopeEntity extends BaseEntity {

    /**
     * 角色编码
     */
	private String roleCode;
    /**
     * 菜单ID
     */
	private Long menuId;
    /**
     * 菜单授权
     */
	private String menuPermissions;
    /**
     * 用户ID
     */
	private Long userId;
    /**
     * 授权类型，1角色授权 2用户授权
     */
	private Integer type;
    /**
     * 租户编码
     */
	private String tenantCode;
}

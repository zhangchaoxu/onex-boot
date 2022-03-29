package com.nb6868.onex.uc.entity;

import com.nb6868.onex.common.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限范围
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_menu_scope")
public class MenuScopeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 角色名
     */
    private String roleName;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 菜单ID
     */
    private String menuPermissions;
    /**
     * 授权类型
     */
    private Integer type;
    /**
     * 用户ID
     */
    private Long userId;

}

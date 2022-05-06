package com.nb6868.onex.common.shiro;

/**
 * 常量
 */
public interface ShiroConst {

    // 用户表
    String TABLE_USER = "uc_user";

    // 角色表
    String TABLE_ROLE = "uc_role";

    // token表
    String TABLE_TOKEN = "uc_token";

    // 菜单权限表
    String TABLE_MENU = "uc_menu";

    // 权限关系表
    String TABLE_MENU_SCOPE = "uc_menu_scope";

    // 用户角色关系表
    String TABLE_USER_ROLE = "uc_role_user";

    // 超级管理员用户类型
    int USER_TYPE_SUPER_ADMIN = 0;

    // 租户管理员-用户类型
    int USER_TYPE_TENANT_ADMIN = 10;

    // 用户状态 有效
    int USER_STATE_ENABLED = 1;

}

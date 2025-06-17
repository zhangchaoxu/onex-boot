package com.nb6868.onex.common.shiro;

/**
 * 常量
 */
public interface ShiroConst {

    // 超级管理员用户类型
    int USER_TYPE_SUPER_ADMIN = 0;

    // 租户管理员-用户类型
    int USER_TYPE_TENANT_ADMIN = 10;

    // 用户状态 有效
    int USER_STATE_ENABLED = 1;

    // 登录已过期
    String MSG_LOGIN_EXPIRED = "登录信息已失效,请重新登录...";
    String MSG_LOGIN_PARAMS_MISS = "缺少登录信息配置,请重新登录...";
    String MSG_LOGIN_USER_LOCKED = "账号已锁定,请联系管理员...";
    String MSG_LOGIN_USER_MISS = "缺少登录账号信息,请重新登录...";
    String MSG_LOGIN_TOKEN_ERROR = "登录信息错误,请重新登录...";
}

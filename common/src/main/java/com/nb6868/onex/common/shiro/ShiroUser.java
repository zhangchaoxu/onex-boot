package com.nb6868.onex.common.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public class ShiroUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String deptCode;
    private String areaCode;
    private String username;
    private String realName;
    private Integer type;
    private String tenantCode;
    // 登录方式
    private String loginType;

    public boolean isFullPermissions() {
        return type == ShiroConst.USER_TYPE_SUPER_ADMIN || type == ShiroConst.USER_TYPE_TENANT_ADMIN;
    }

    public boolean isFullRoles() {
        return type == ShiroConst.USER_TYPE_SUPER_ADMIN || type == ShiroConst.USER_TYPE_TENANT_ADMIN;
    }

}

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
    private String username;
    private String realName;
    private Integer type;
    private String tenantCode;
    private String loginType;

    public boolean isFullPermissions () {
        return type == ShiroConst.USER_TYPE_SUPERADMIN;
    }

    public boolean isFullRoles () {
        return type == ShiroConst.USER_TYPE_SUPERADMIN;
    }

}

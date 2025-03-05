package com.nb6868.onex.common.shiro;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
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
    private Integer type;
    private String code;
    private String mobile;
    private String deptCode;
    private String areaCode;
    private String postCode;
    private String username;
    private String realName;
    private String tenantCode;
    private JSONObject extInfo;
    // 登录方式
    private String loginType;
    // 登录配置
    private JSONObject loginConfig;

    public boolean isFullPermissions() {
        return type == ShiroConst.USER_TYPE_SUPER_ADMIN || type == ShiroConst.USER_TYPE_TENANT_ADMIN;
    }

    public boolean isFullRoles() {
        return type == ShiroConst.USER_TYPE_SUPER_ADMIN || type == ShiroConst.USER_TYPE_TENANT_ADMIN;
    }

    /**
     * 获得合适的显示用名称，先找realName,找不到用username
     */
    public String getNamePretty() {
        return StrUtil.blankToDefault(realName, username);
    }

}

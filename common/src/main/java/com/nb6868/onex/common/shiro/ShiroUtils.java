package com.nb6868.onex.common.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ShiroUtils {

    public static Subject getSubject() {
        try {
            return SecurityUtils.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户信息
     */
    public static ShiroUser getUser() {
        Subject subject = getSubject();
        if (subject == null) {
            return new ShiroUser();
        }

        ShiroUser user = (ShiroUser) subject.getPrincipal();
        if (user == null) {
            return new ShiroUser();
        }

        return user;
    }

    /**
     * 获取用户Id
     */
    public static Long getUserId() {
        return getUser().getId();
    }

    /**
     * 获得用户用户名
     */
    public static String getUserUsername() {
        return getUser().getUsername();
    }

    /**
     * 获得用户真实姓名
     */
    public static String getUserRealName() {
        return getUser().getRealName();
    }

    /**
     * 获得合适的显示用名称，先找realName,找不到用username
     */
    public static String getUserNamePretty() {
        return getUser().getNamePretty();
    }

    /**
     * 获得用户租户编码
     */
    public static String getUserTenantCode() {
        return getUser().getTenantCode();
    }

    /**
     * 获得用户区域编码
     */
    public static String getUserAreaCode() {
        return getUser().getAreaCode();
    }

    /**
     * 获得用户组织编码
     */
    public static String getUserDeptCode() {
        return getUser().getDeptCode();
    }

}

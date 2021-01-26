package com.nb6868.onexboot.api.modules.uc;

/**
 * 用户相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface UcConst {

    /**
     * 匿名角色编码
     */
    String ROLE_CODE_ANON = "ANON";
    /**
     * 匿名用户token
     */
    String TOKEN_ANON = "00000000000000000000000000000000";
    /**
     * 管理平台登录配置
     */
    String LOGIN_ADMIN = "LOGIN_ADMIN";

    /**
     * 登录配置前缀
     */
    String LOGIN_TYPE_PREFIX = "LOGIN_TYPE_";

    /**
     * token header
     */
    String TOKEN_HEADER = "token";

    /**
     * 微信公众号配置项默认key
     */
    String WX_CFG_MP = "WX_CFG_MP";

    /**
     * 微信小程序配置项默认key
     */
    String WX_CFG_MA = "WX_CFG_MA";

    /**
     * 微信session中的openid
     */
    String WX_SESSION_OPEN_ID = "WX_OPENID";

    /**
     * 部门最大等级限制
     */
    int DEPT_HIERARCHY_MAX = 10;

    /**
     * 用户状态
     */
    enum UserStatusEnum {

        /**
         * 详见name
         */
        PENDING(-1, "待审核"),
        DISABLE(0, "冻结"),
        ENABLED(1, "正常");

        private int value;
        private String name;

        UserStatusEnum(int value) {
            this.value = value;
        }

        UserStatusEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * 用户类型
     */
    enum UserTypeEnum {

        /**
         * 详见name
         */
        ADMIN(0, "超级管理员"),
        SYSADMIN(10, "系统管理员"),
        DEPTADMIN(20, "单位管理员"),
        USER(100, "用户");

        private int value;
        private String name;

        UserTypeEnum(int value) {
            this.value = value;
        }

        UserTypeEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

    }

    /**
     * 菜单类型枚举
     */
    enum MenuTypeEnum {
        /**
         * 菜单
         */
        MENU(0),
        /**
         * 按钮
         */
        BUTTON(1),
        /**
         * 页面
         */
        PAGE(1);

        private int value;

        MenuTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 第三方登录类型枚举
     */
    enum OauthTypeEnum {
        /**
         * 苹果登录
         */
        APPLE(),
        /**
         * 微信APP
         */
        WECHAT_APP(),
        /**
         * 微信网站应用
         */
        WECHAT_WEB(),
        /**
         * 微信公众帐号
         */
        WECHAT_MP(),
        /**
         * 微信小程序
         */
        WECHAT_MA(),
        /**
         * 微信第三方应用
         */
        WECHAT_THIRD(),
        /**
         * 钉钉扫码登录
         */
        DINGTALK_SCAN();

    }

    /**
     * 登录类型
     */
    enum LoginTypeEnum {

        /**
         * 退出
         */
        LOGOUT,
        // 后台帐号密码登录
        ADMIN_USERNAME_PASSWORD,
        // 后台手机密码登录
        ADMIN_MOBILE_PWD,
        // 后台手机短信登录
        ADMIN_MOBILE_SMSCODE,
        // 后台微信登录
        ADMIN_WECHAT,
        // 后台钉钉扫码登录
        ADMIN_DINGTALK_SCAN,
        // APP帐号密码登录
        APP_USER_PWD,
        // APP手机密码登录
        APP_MOBILE_PWD,
        // APP手机短信登录
        APP_MOBILE_SMS,
        // APP微信登录
        APP_WECHAT,
        // APP苹果登录
        APP_APPLE,
        // APP苹果登录验证手机号
        APP_APPLE_MOBILE_SMS;
    }

}

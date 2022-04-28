package com.nb6868.onex.uc;

/**
 * 用户中心相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface UcConst {

    /**
     * 默认租户
     */
    String TENANT_CODE_DEFAULT = "default";

    /**
     * 登录配置
     */
    String PARAMS_CODE_LOGIN = "LOGIN";

    /**
     * 部门最大等级限制
     */
    int DEPT_HIERARCHY_MAX = 100;
    /**
     * 用户状态
     */
    enum UserStateEnum {

        /**
         * 详见name
         */
        PENDING(-1, "待审核"),
        DISABLE(0, "冻结"),
        ENABLED(1, "正常");

        private int value;
        private String name;

        UserStateEnum(int value) {
            this.value = value;
        }

        UserStateEnum(int value, String name) {
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
     * 参数类型
     */
    enum ParamsTypeEnum {

        /**
         * 详见name
         */
        SYSTEM(0, "系统参数"),
        TENANT(1, "租户参数"),
        USER(2, "用户参数");

        private int value;
        private String name;

        ParamsTypeEnum(int value) {
            this.value = value;
        }

        ParamsTypeEnum(int value, String name) {
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
     * 参数范围
     */
    enum ParamsScopeEnum {

        /**
         * 详见name
         */
        PUBLIC("public", "公开"),
        TENANT("private", "私有");

        private String value;
        private String name;

        ParamsScopeEnum(String value) {
            this.value = value;
        }

        ParamsScopeEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * 权限范围类型
     */
    enum MenuScopeTypeEnum {

        /**
         * 详见name
         */
        ROLE(1, "角色"),
        USER(2, "用户");

        private int value;
        private String name;

        MenuScopeTypeEnum(int value) {
            this.value = value;
        }

        MenuScopeTypeEnum(int value, String name) {
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
        SUPER_ADMIN(0, "超级管理员"),
        TENANT_ADMIN(1, "管理员"),
        DEPT_ADMIN(20, "单位管理员"),
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

}

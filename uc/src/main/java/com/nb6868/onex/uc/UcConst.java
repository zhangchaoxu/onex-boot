package com.nb6868.onex.uc;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 用户中心相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface UcConst {

    /**
     * 部门根节点标识
     */
    Long DEPT_ROOT = 0L;
    /**
     * 部门最大等级限制
     */
    int DEPT_HIERARCHY_MAX = 100;
    /**
     * 用户状态
     */
    @Getter
    @AllArgsConstructor
    enum UserStateEnum {

        /**
         * 详见name
         */
        PENDING(-1, "待审核"),
        DISABLE(0, "冻结"),
        ENABLED(1, "正常");

        private int code;
        private String title;

        public static UserStateEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            UserStateEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 参数类型
     */
    @Getter
    @AllArgsConstructor
    enum ParamsTypeEnum {

        /**
         * 详见name
         */
        SYSTEM(0, "系统参数"),
        TENANT(1, "租户参数"),
        USER(2, "用户参数");

        private int code;
        private String title;

        public static ParamsTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            ParamsTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 参数范围
     */
    @Getter
    @AllArgsConstructor
    enum ParamsScopeEnum {

        /**
         * 详见name
         */
        PUBLIC("public", "公开"),
        PRIVATE("private", "私有");

        private String code;
        private String title;

        public static ParamsScopeEnum findByCode(String codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(String codeValue) {
            ParamsScopeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(String codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 角色用户关系类型
     */
    @Getter
    @AllArgsConstructor
    enum RoleUserTypeEnum {

        /**
         * 详见name
         */
        DEFAULT(0, "默认"),
        SPECIAL(1, "特殊关系,预留");

        private int code;
        private String title;

        public static RoleUserTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            RoleUserTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }

    }

    /**
     * 部门用户关系类型
     */
    @Getter
    @AllArgsConstructor
    enum DeptUserTypeEnum {

        /**
         * 详见name秒，还有其他的自行扩展
         */
        DEFAULT(0, "隶属该部门"),
        IN_CHARGE(1, "负责该部门"),
        LEADER(2, "领导该部门");

        private int code;
        private String title;

        public static DeptUserTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            DeptUserTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 权限范围类型
     */
    @Getter
    @AllArgsConstructor
    enum MenuScopeTypeEnum {

        /**
         * 详见name
         */
        ROLE(1, "角色"),
        USER(2, "用户");

        private int code;
        private String title;

        public static MenuScopeTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            MenuScopeTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 用户类型
     */
    @Getter
    @AllArgsConstructor
    enum UserTypeEnum {

        /**
         * 详见name
         */
        SUPER_ADMIN(0, "超级管理员"),
        TENANT_ADMIN(10, "租户管理员"),
        DEPT_ADMIN(20, "单位管理员"),
        USER(100, "用户");

        private int code;
        private String title;

        public static UserTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            UserTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }

    }

    /**
     * 菜单类型枚举
     */
    @Getter
    @AllArgsConstructor
    enum MenuTypeEnum {

        MENU(0, "菜单/页面"),
        BUTTON(1, "按钮/权限");

        private int code;
        private String title;

        public static MenuTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            MenuTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }

    }

}

package com.nb6868.onex.sys;

/**
 * 系统模块常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface SysConst {

    String OSS_PUBLIC = "OSS_PUBLIC";

    /**
     * 日志类型
     */
    enum LogTypeEnum {

        /**
         * 详见name
         */
        LOGIN("login", "登录"),
        OPERATION("operation", "操作"),
        ERROR("error", "错误");

        private String value;
        private String name;

        LogTypeEnum(String value) {
            this.value = value;
        }

        LogTypeEnum(String value, String name) {
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

}

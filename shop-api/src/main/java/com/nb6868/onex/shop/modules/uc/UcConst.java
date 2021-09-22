package com.nb6868.onex.shop.modules.uc;

/**
 * 用户相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface UcConst {

    // 用户角色关系表
    String TABLE_USER_ROLE = "uc_role_user";

    // 用户表
    String TABLE_USER = "uc_user";

    // token表
    String TABLE_TOKEN = "uc_token";

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

}

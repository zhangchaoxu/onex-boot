package com.nb6868.onexboot.api.modules.msg;

/**
 * 消息模块常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MsgConst {

    /**
     * 验证码短信模板前缀
     */
    String SMS_CODE_TPL_PREFIX = "CODE_";

    /**
     * 短信验证码模板-通用
     */
    String SMS_TPL_CODE_COMMON = SMS_CODE_TPL_PREFIX + "COMMON";
    /**
     * 短信验证码模板-登录
     */
    String SMS_TPL_LOGIN = SMS_CODE_TPL_PREFIX + "LOGIN";
    /**
     * 短信验证码模板-修改密码
     */
    String SMS_TPL_CHANGE_PASSWORD = SMS_CODE_TPL_PREFIX + "CHANGE_PASSWORD";
    /**
     * 短信验证码模板-注册
     */
    String SMS_TPL_REGISTER = SMS_CODE_TPL_PREFIX+  "REGISTER";

    /**
     * 消息类型
     */
    enum MailTypeEnum {

        /**
         * 支持的消息类型定义
         */
        SMS("短信"),
        EMAIL("电子邮件"),
        WX_MP_TEMPLATE("微信公众号模板消息"),
        WX_MA_SUBSCRIBE("微信小程序订阅消息"),
        APP_PUSH("APP推送");

        private String code;

        MailTypeEnum(String code) {
            this.code = code;
        }

    }

    /**
     * 推送类型
     */
    enum PushTypeEnum {
        /**
         * 全部推送
         */
        ALL(10),
        /**
         * 按alias推送
         */
        ALIAS(20),
        /**
         * 按tags推送
         */
        TAGS(30),
        /**
         * 按alias和alias的交集推送
         */
        ALIAS_AND_TAGS(40);

        private int value;

        PushTypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

}

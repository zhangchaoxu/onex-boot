package com.nb6868.onex.msg;

/**
 * 消息模块常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MsgConst {

    /**
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
     * 模板参数配置错误
     */
    String MAIL_TPL_PARAMS_ERROR = "请检查消息模板参数配置";

    /**
     * 消息渠道类型
     */
    enum MailChannelEnum {

        /**
         * 消息渠道类型
         */
        SMS("短信"),
        EMAIL("电子邮件"),
        DINGTALK("钉钉"),
        WX_MP_TEMPLATE("微信公众号模板消息"),
        WX_MA_SUBSCRIBE("微信小程序订阅消息");

        private String code;

        MailChannelEnum(String code) {
            this.code = code;
        }

    }

    /**
     * 消息类型
     */
    enum MailTypeEnum {

        /**
         * 支持的消息类型定义
         */
        CODE(1, "验证码"),
        NOTIFY(2, "通知"),
        ADV(3, "营销广告");

        private Integer value;
        private String name;

        MailTypeEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 消息发送状态
     */
    enum MailSendStateEnum {

        /**
         * 支持的消息类型定义
         */
        SENDING(0, "发送中"),
        SUCCESS(1, "发送成功"),
        FAIL(-1, "发送失败");

        private Integer value;
        private String name;

        MailSendStateEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }
    }

}

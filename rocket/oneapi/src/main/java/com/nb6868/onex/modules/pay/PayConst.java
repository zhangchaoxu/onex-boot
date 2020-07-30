package com.nb6868.onex.modules.pay;

import java.util.Arrays;
import java.util.Optional;

/**
 * 商城相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface PayConst {

    /**
     * 支付类型枚举
     * 0 无须支付 1 现金交易 2 银行转账 3 支付宝支付 4 微信支付
     */
    enum PayTypeEnum {

        /**
         * 详见name
         */
        NO_PAY(0, "无须支付"),
        CASH(1, "现金"),
        BANK(2, "银行"),
        ALIPAY(3, "支付宝"),
        WECHAT(4, "微信"),
        BALANCE(5, "余额");

        private int value;
        private String name;

        PayTypeEnum(int value) {
            this.value = value;
        }

        PayTypeEnum(int value, String name) {
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
     * 支付状态类型
     * 0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成
     */
    enum PayStatusEnum {

        /**
         * 详见name
         */
        UNKNOWN(-100, "未知"),
        NO_PAY(0, "待支付"),
        PAYING(1, "支付中(目前未使用)"),
        PAID(2, "支付成功"),
        BIZ_HANDLED(3, "业务处理完成");

        private int value;
        private String name;

        PayStatusEnum(int value) {
            this.value = value;
        }

        PayStatusEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

        public static Optional<PayStatusEnum> getOptionalByValue(int value) {
            return Arrays.stream(values()).filter(bl -> bl.value() == value).findFirst();
        }

        public static PayStatusEnum getByValue(int value) {
            return Arrays.stream(values()).filter(bl -> bl.value() == value).findFirst().orElse(UNKNOWN);
        }

    }

}

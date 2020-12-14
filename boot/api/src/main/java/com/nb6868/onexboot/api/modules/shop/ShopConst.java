package com.nb6868.onexboot.api.modules.shop;

import java.util.Arrays;
import java.util.Optional;

/**
 * 商城相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ShopConst {

    /**
     * 订单状态类型
     */
    enum OrderStatusEnum {

        /**
         * 详见name
         */
        UNKNOWN(-100, "未知"),
        CANCELED(-10, "已取消"),
        PLACED(0, "已下单"),
        PAID(5, "已支付"),
        CONFIRMED(10, "已确认"),
        COMPLETED(100, "已完成");

        private int value;
        private String name;

        OrderStatusEnum(int value) {
            this.value = value;
        }

        OrderStatusEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

        public static Optional<OrderStatusEnum> getOptionalByValue(int value) {
            return Arrays.stream(values()).filter(bl -> bl.value() == value).findFirst();
        }

        public static OrderStatusEnum getByValue(int value) {
            return Arrays.stream(values()).filter(bl -> bl.value() == value).findFirst().orElse(UNKNOWN);
        }

    }

    /**
     * 订单支付状态类型
     */
    enum OrderPayStatusEnum {

        /**
         * 详见name
         */
        UNKNOWN(-100, "未知"),
        REFUND_ALL(-10, "已全部退款"),
        REFUND_PART(-5, "已全部退款"),
        REFUND_ING(-1, "退款中"),
        NO_PAY(0, "未支付"),
        PAID(1, "已支付");

        private int value;
        private String name;

        OrderPayStatusEnum(int value) {
            this.value = value;
        }

        OrderPayStatusEnum(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int value() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

        public static Optional<OrderPayStatusEnum> getOptionalByValue(int value) {
            return Arrays.stream(values()).filter(bl -> bl.value() == value).findFirst();
        }

        public static OrderPayStatusEnum getByValue(int value) {
            return Arrays.stream(values()).filter(bl -> bl.value() == value).findFirst().orElse(UNKNOWN);
        }

    }

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
     * 分销类型
     */
    enum DistTypeEnum {

        /**
         * 详见name
         */
        NONE(0, "不参与"),
        SCALE(1, "按比例"),
        FIX_VALUE(2, "固定值"),
        RANDOM(3, "随机");

        private int value;
        private String name;

        DistTypeEnum(int value) {
            this.value = value;
        }

        DistTypeEnum(int value, String name) {
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
     * 进出库记录类型
     */
    enum StockLogTypeEnum {

        /**
         * 详见name
         */
        IN(0, "入库"),
        OUT(1, "出库");

        private int value;
        private String name;

        StockLogTypeEnum(int value) {
            this.value = value;
        }

        StockLogTypeEnum(int value, String name) {
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

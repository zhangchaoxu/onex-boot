package com.nb6868.onex.modules.wx;

/**
 * 微信相关常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface WxConst {

    /**
     * 应用类型
     */
    enum AppTypeEnum {

        /**
         * 详见name
         */
        APP(1, "移动应用"),
        WEB(2, "网站应用"),
        MP(3, "公众帐号"),
        MA(4, "小程序"),
        THIRD_PRAT(5, "第三方应用");

        private int value;
        private String name;

        AppTypeEnum(int value) {
            this.value = value;
        }

        AppTypeEnum(int value, String name) {
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

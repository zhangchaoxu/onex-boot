package com.nb6868.onexboot.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    /**
     * 获取区间内一个随机2位小数
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static BigDecimal getRandom(BigDecimal min, BigDecimal max) {
        int maxInt = max.multiply(new BigDecimal("100")).intValue(), minInt = min.multiply(new BigDecimal("100")).intValue();
        int ran = (int) (Math.random() * (maxInt - minInt) + minInt);
        return new BigDecimal(ran).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}

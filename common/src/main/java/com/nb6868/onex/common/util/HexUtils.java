package com.nb6868.onex.common.util;

/**
 * 进制转换工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class HexUtils {

    /**
     * 62进制字符
     */
    private static final String BASE_62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 16进制字符
     */
    private static final String BASE_16 = "0123456789abcdef";

    /**
     * 10进制字符
     */
    private static final String BASE_10 = "0123456789";

    /**
     * 将10进制转化为62进制
     *
     * @param number 十进制数
     * @return 转换后的62进制
     */
    public static String convertBase10To62(long number) {
        char[] digitsChar = BASE_62.toCharArray();
        StringBuilder buf = new StringBuilder();
        while (number != 0) {
            // 获取余数
            buf.append(digitsChar[(int) (number % 62)]);
            // 剩下的值
            number /= 62;
        }
        // 反转
        return buf.reverse().toString();
    }

}

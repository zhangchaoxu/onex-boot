package com.nb6868.onexboot.common.util;

import lombok.SneakyThrows;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

/**
 * 字符处理工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class StringUtils {

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
     * 默认分隔符
     */
    private static final String SEPARATOR = ",";

    /**
     * 拆分文本
     * trimResults 拆分去除前后空格
     * omitEmptyStrings 去除拆分出来空的字符串
     *
     * @param sequence 字符串
     * @return 拆分文本
     */
    public static List<String> splitToList(final CharSequence sequence) {
        return splitToList(sequence, SEPARATOR);
    }

    /**
     * 拆分文本
     * trimResults 拆分去除前后空格
     * omitEmptyStrings 去除拆分出来空的字符串
     *
     * @param sequence  字符串
     * @param separator 分隔符
     * @return 拆分文本
     */
    public static List<String> splitToList(final CharSequence sequence, String separator) {
        List<String> stringList = new ArrayList<>();
        if (isNotBlank(sequence)) {
            String[] strings = sequence.toString().trim().split(separator);
            for (String string : strings) {
                if (isNotBlank(string)) {
                    // trimResults 拆分去除前后空格
                    // omitEmptyStrings 去除拆分出来空的字符串
                    stringList.add(string.trim());
                }
            }
        }
        return stringList;
    }

    /**
     * 拆分文本
     *
     * @param sequence  字符串
     * @param separator 分隔符
     * @return 拆分文本
     */
    public static String[] split(final CharSequence sequence, String separator) {
        if (isNotBlank(sequence)) {
            return sequence.toString().trim().split(separator);
        } else {
            return new String[]{};
        }
    }

    public static String[] split(final CharSequence sequence) {
        return split(sequence, SEPARATOR);
    }

    /**
     * 用分隔符拼接数组
     * @param list 数组
     * @return 拼接后的字符串
     */
    public static String joinList(Object[] list) {
        return joinList(list, ",");
    }

    /**
     * 用分隔符拼接数组
     * @param list 数组
     * @param separator 字符串
     * @return 拼接后的字符串
     */
    public static String joinList(Object[] list, String separator) {
        StringJoiner stringJoiner = new StringJoiner(separator);
        for (Object str : list) {
            stringJoiner.add(str.toString());
        }
        return stringJoiner.toString();
    }

    /**
     * 是否为空
     *
     * @param cs
     * @return
     */
    public static boolean isBlank(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    /**
     * 是否不为空
     *
     * @param cs
     * @return
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNotBlank(cs);
    }

    /**
     * 判断是否为空
     *
     * @param cs
     * @return
     */
    public static boolean isEmpty(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isEmpty(cs);
    }

    /**
     * 判断是否不为空
     *
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(cs);
    }

    /**
     * 获得随机62进制
     *
     * @param num 长度
     * @return 随机字符
     */
    public static String getRandomBase62(Integer num) {
        return getRandomString(BASE_62, num);
    }

    /**
     * 获得随机16进制
     *
     * @param num 长度
     * @return 随机字符
     */
    public static String getRandomHex(Integer num) {
        return getRandomString(BASE_16, num);
    }

    /**
     * 获得随机10进制
     *
     * @param num 长度
     * @return 随机字符
     */
    public static String getRandomDec(Integer num) {
        return getRandomString(BASE_10, num);
    }

    /**
     * 获得随机字符
     *
     * @param num 长度
     * @return 随机字符
     */
    public static String getRandomString(String base, Integer num) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

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

    /**
     * hex转byte数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexToByte(String hex) {
        int m, n;
        // 每两个字符描述一个字节
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }

    /**
     * byte数组转hex
     *
     * @param bytes
     * @return
     */
    public static String byteToHex(byte[] bytes) {
        List<String> array = byteToHexArray(bytes);
        return org.apache.commons.lang3.StringUtils.join(array, "");
    }

    /**
     * byte数组转hex数组
     *
     * @param bytes
     * @return
     */
    public static List<String> byteToHexArray(byte[] bytes) {
        String strHex;
        List<String> array = new ArrayList<>();
        for (byte aByte : bytes) {
            strHex = Integer.toHexString(aByte & 0xFF);
            // 每个字节由两个字符表示，位数不够，高位补0
            array.add((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return array;
    }

    /**
     * URLDecoder
     */
    @SneakyThrows(UnsupportedEncodingException.class)
    public static String urlDecode(String raw) {
        return URLDecoder.decode(raw, StandardCharsets.UTF_8.name());
    }

    /**
     * URLEncoder
     */
    @SneakyThrows(UnsupportedEncodingException.class)
    public static String urlEncode(String raw) {
        return URLEncoder.encode(raw, StandardCharsets.UTF_8.name());
    }

}

package com.nb6868.onex.common.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class PasswordUtils {

    // 8-20个字符，至少包含数字、字母和特殊字符中的两种
    public static String PATTERN_RULE_0 = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";
    // 至少八个字符，至少一个字母，一个数字
    public static String PATTERN_RULE_1 = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    // 至少八个字符，至少一个字母，一个数字和一个特殊字符
    public static String PATTERN_RULE_2 = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
    // 最少八个字符，至少一个大写字母，一个小写字母和一个数字
    public static String PATTERN_RULE_3 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    // 至少八个字符，至少一个大写字母，一个小写字母，一个数字和一个特殊字符
    public static String PATTERN_RULE_4 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}";
    // 8-20个字符，至少一个字母和一个数字
    public static String PATTERN_RULE_5 = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
    // 8-20个字符，至少一个字母，一个数字和一个特殊字符
    public static String PATTERN_RULE_6 = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";
    // 8-20个字符，至少一个大写字母，一个小写字母和一个数字
    public static String PATTERN_RULE_7 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
    // 8-20个字符，至少一个大写字母，一个小写字母，一个数字和一个特殊字符
    public static String PATTERN_RULE_8 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,20}";

    /**
     * 可逆AES加密
     * @param raw 明文
     * @param key aes密钥
     * @return 密文
     */
    public static String aesEncode(String raw, String key) {
        return SecureUtil.aes(key.getBytes()).encryptBase64(raw);
    }

    /**
     * 可逆AES解密
     * @param password 密文
     * @param key aes密钥
     * @return 明文
     */
    public static String aesDecode(String password, String key) {
        return SecureUtil.aes(key.getBytes()).decryptStr(password);
    }

    /**
     * 加密
     *
     * @param raw 字符串
     * @return 返回加密字符串
     */
    public static String encode(String raw) {
        return encode(raw, "bcrypt");
    }

    /**
     * 比较密码是否相等
     *
     * @param raw     明文密码
     * @param encoded 加密后密码
     * @return 匹配结果
     */
    public static boolean verify(String raw, String encoded) {
        return verify(raw, encoded, "bcrypt");
    }

    /**
     * 加密
     *
     * @param raw    字符串
     * @param secure 算法
     * @return 返回加密字符串
     */
    public static String encode(String raw, String secure) {
        if ("sm3".equalsIgnoreCase(secure)) {
            return SmUtil.sm3(raw);
        }
        return DigestUtil.bcrypt(raw);
    }

    /**
     * 比较密码是否相等
     *
     * @param raw     明文密码
     * @param encoded 加密后密码
     * @return 匹配结果
     */
    public static boolean verify(String raw, String encoded, String secure) {
        if ("sm3".equalsIgnoreCase(secure)) {
            return encoded.equalsIgnoreCase(encode(raw, secure));
        }
        return DigestUtil.bcryptCheck(raw, encoded);
    }

}

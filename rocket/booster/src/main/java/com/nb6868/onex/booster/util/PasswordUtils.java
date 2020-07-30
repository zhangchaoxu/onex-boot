package com.nb6868.onex.booster.util;

import com.nb6868.onex.booster.util.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class PasswordUtils {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密
     *
     * @param raw 字符串
     * @return 返回加密字符串
     */
    public static String encode(String raw) {
        return passwordEncoder.encode(raw);
    }


    /**
     * 比较密码是否相等
     *
     * @param raw     明文密码
     * @param encoded 加密后密码
     * @return true：成功    false：失败
     */
    public static boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }


    public static void main(String[] args) {
        String str = "admin";
        String password = encode(str);

        System.out.println(password);
        System.out.println(matches(str, password));
    }

}

package com.nb6868.onexboot.common.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class PasswordUtils {

    /**
     * 加密
     *
     * @param raw 字符串
     * @return 返回加密字符串
     */
    public static String encode(String raw) {
        return DigestUtil.bcrypt(raw);
    }

    /**
     * 比较密码是否相等
     *
     * @param raw     明文密码
     * @param encoded 加密后密码
     * @return 匹配结果
     */
    public static boolean matches(String raw, String encoded) {
        return DigestUtil.bcryptCheck(raw, encoded);
    }

}

package com.nb6868.onex.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTException;

import java.util.Date;

/**
 * jwt 工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class JwtUtils {

    /**
     * 解析token
     */
    public static JWT parseToken(String token) {
        try {
            return JWT.of(token);
        } catch (JWTException e) {
            return null;
        }
    }

    /**
     * 验证密码和时间
     */
    public static boolean verifyKeyAndExp(String token, String key) {
        if (StrUtil.isBlank(token) || StrUtil.isBlank(key)) {
            return false;
        }
        JWT jwt = parseToken(token);
        return null != jwt && jwt.setKey(token.getBytes()).verify() && new Date(jwt.getPayload().getClaimsJson().getLong("exp") * 1000).after(new Date());
    }

    /**
     * 验证密码
     */
    public static boolean verifyKey(String token, byte[] key) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        JWT jwt = parseToken(token);
        return null != jwt && jwt.setKey(key).verify();
    }
}

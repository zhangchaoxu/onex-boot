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
        if (StrUtil.isBlank(token)) {
            return null;
        }
        try {
            return JWT.of(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证密码和时间
     */
    public static boolean verifyKeyAndExp(String token, byte[] key) {
        JWT jwt = parseToken(token);
        return null != jwt && jwt.setKey(key).verify() && new Date(jwt.getPayload().getClaimsJson().getLong("exp") * 1000).after(new Date());
    }

    /**
     * 验证密码
     */
    public static boolean verifyKey(String token, byte[] key) {
        JWT jwt = parseToken(token);
        return null != jwt && jwt.setKey(key).verify();
    }
}

package com.nb6868.onex.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
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
    public static boolean verifyKeyAndExp(JWT jwt, String key) {
        if (null == jwt || StrUtil.isBlank(key)) {
            return false;
        }
        if (!jwt.setKey(key.getBytes()).verify()) {
            return false;
        } else {
            JSONObject claimsJson = jwt.getPayload().getClaimsJson();
            return claimsJson != null && claimsJson.getLong("exp") != null && new Date(claimsJson.getLong("exp") * 1000).after(new Date());
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
        if (null == jwt || !jwt.setKey(key.getBytes()).verify()) {
            return false;
        } else {
            JSONObject claimsJson = jwt.getPayload().getClaimsJson();
            return claimsJson != null && claimsJson.getLong("exp") != null && new Date(claimsJson.getLong("exp") * 1000).after(new Date());
        }
    }

    /**
     * 验证密码
     */
    public static boolean verifyKey(String token, String key) {
        if (StrUtil.isBlank(token) || StrUtil.isBlank(key)) {
            return false;
        }
        JWT jwt = parseToken(token);
        return null != jwt && jwt.setKey(key.getBytes()).verify();
    }

    /**
     * 验证密码
     */
    public static boolean verifyKey(JWT jwt, String key) {
        return jwt != null && StrUtil.isNotBlank(key) && jwt.setKey(key.getBytes()).verify();
    }


}

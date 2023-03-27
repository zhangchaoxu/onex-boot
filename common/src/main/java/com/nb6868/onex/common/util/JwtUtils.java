package com.nb6868.onex.common.util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

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
            // 先验证一下
            return false;
        }
        JSONObject claimsJson = jwt.getPayload().getClaimsJson();
        if (claimsJson == null || claimsJson.get("exp") == null) {
            return false;
        }
        try {
            return  new Date(NumberUtil.parseLong(claimsJson.get("exp").toString()) * 1000).after(new Date());
        } catch (Exception e) {
            return false;
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
        return verifyKeyAndExp(jwt, key);
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

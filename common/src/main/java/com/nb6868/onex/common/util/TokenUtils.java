package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthProps;

import java.util.Date;
import java.util.Map;

/**
 * Token生成器
 */
public class TokenUtils {

    /**
     * 生成token
     *
     * @param authConfig 登录策略
     * @param params     payload参数
     * @return jwt
     */
    public static String getToken(AuthProps.Config authConfig, Map<String, Object> params) {
        Date now = new Date();
        JWT jwt = JWT.create()
                .setSubject(authConfig.getType())
                .setKey(authConfig.getTokenKey().getBytes())
                .setIssuedAt(now)
                .setNotBefore(now)
                // jwt99年过期=永不过期
                .setExpiresAt(authConfig.getTokenExpire() == -1 ? DateUtil.offsetMonth(now, 12 * 99) : DateUtil.offsetSecond(now, authConfig.getTokenExpire()));
        params.forEach(jwt::setPayload);
        return jwt.sign();
    }

}

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
        if ("uuid".equalsIgnoreCase(authConfig.getTokenPolicy())) {
            return IdUtil.simpleUUID();
        } else {
            JWT jwt = JWT.create()
                    .setSubject(authConfig.getType())
                    .setKey(authConfig.getTokenKey().getBytes());
            if (authConfig.getTokenExpire() == -1) {
                // jwt99年过期=永不过期
                jwt.setExpiresAt(DateUtil.offsetMonth(new Date(), 12 * 99));
            } else {
                jwt.setExpiresAt(DateUtil.offsetSecond(new Date(), authConfig.getTokenExpire()));
            }
            params.forEach(jwt::setPayload);
            return jwt.sign();
        }
    }

}

package com.nb6868.onex.uc.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.dao.TokenDao;
import com.nb6868.onex.uc.entity.TokenEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TokenService extends EntityService<TokenDao, TokenEntity> {

    /**
     * 生成token
     *
     * @param user        用户
     * @param tokenStoreType token存储类型
     * @param loginType 登录类型
     * @param tokenKey token密钥
     * @param tokenExpire 有效期(秒) 小等于 0 表示不限
     * @param tokenLimit token限制策略 0不限制 1同type只允许一个 2同用户只允许一个
     * @return result
     */
    public String createToken(UserEntity user, String tokenStoreType, String loginType, String tokenKey, int tokenExpire, int tokenLimit) {
        // 生成token
        Date now = new Date();
        // 1个世纪=永不过期
        Date expireDate = tokenExpire <= 0 ? DateUtil.offset(now, DateField.ERA, 1) : DateUtil.offsetSecond(now, tokenExpire);
        // 注意同一秒内生成的token是一致的
        String jwtToken = JWT.create()
                .setSubject(loginType)
                .setKey(tokenKey.getBytes())
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiresAt(expireDate)
                .setPayload("id", user.getId())
                .setPayload("type", user.getType())
                .setPayload("deptCode", user.getDeptCode())
                .setPayload("areaCode", user.getAreaCode())
                .setPayload("postCode", user.getPostCode())
                .setPayload("tenantCode", user.getTenantCode())
                .setPayload("username", user.getUsername())
                .setPayload("realName", user.getRealName())
                .sign();
        // 判断一下token是否已存在
        if ("db".equalsIgnoreCase(tokenStoreType) && !query().eq("token", jwtToken).exists()) {
            // 在数据库中
            if (1 == tokenLimit) {
                // 同type只允许一个
                deleteTokenByUserId(user.getId(), loginType);
            } else if (2 == tokenLimit) {
                // 不支持多端登录,注销该用户所有token
                // 2同用户只允许一个
                deleteByUserIdList(Collections.singletonList(user.getId()));
            }
            // 不管逻辑，永远都是重新生成一个token
            TokenEntity tokenEntity = new TokenEntity();
            tokenEntity.setUserId(user.getId());
            tokenEntity.setTenantCode(user.getTenantCode());
            tokenEntity.setToken(jwtToken);
            tokenEntity.setType(loginType);
            tokenEntity.setExpireTime(expireDate);
            this.save(tokenEntity);
        }
        return jwtToken;
    }

    /**
     * 注销token
     *
     * @param token 用户token
     * @return result
     */
    public boolean deleteToken(String token) {
        return logicDeleteByWrapper(update().eq("token", token));
    }

    /**
     * 删除用户下所有token
     *
     * @param userId 用户id
     * @param type   登录类型
     * @return result
     */
    public boolean deleteTokenByUserId(Long userId, String type) {
        return logicDeleteByWrapper(update()
                .eq("user_id", userId)
                .eq(StrUtil.isNotBlank(type), "type", type));
    }

    /**
     * 删除用户token
     *
     * @param userIds 用户ID数组
     * @return result
     */
    public boolean deleteByUserIdList(List<Long> userIds) {
        return logicDeleteByWrapper(update().in("user_id", userIds));
    }

}

package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.uc.dao.TokenDao;
import com.nb6868.onexboot.api.modules.uc.dto.LoginTypeConfig;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.service.TokenService;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.service.impl.BaseServiceImpl;
import com.nb6868.onexboot.common.util.IdUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class TokenServiceImpl extends BaseServiceImpl<TokenDao, TokenEntity> implements TokenService {

    @Override
    public String createToken(Long userId, LoginTypeConfig loginTypeConfig) {
        // 当前时间
        Date now = new Date();
        // 过期时间
        Date expireTime = new Date(now.getTime() + loginTypeConfig.getExpire() * 1000);
        // 生成的token
        if (loginTypeConfig.isMultiLogin()) {
            // 支持多点登录
        } else {
            // 不支持多点登录,注销该用户所有token
            deleteTokenByUserId(userId, loginTypeConfig.getType());
        }
        // 不管逻辑，永远都是重新生成一个token
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(IdUtils.simpleUUID());
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(expireTime);
        tokenEntity.setType(loginTypeConfig.getType());

        // 保存token
        this.save(tokenEntity);

        return tokenEntity.getToken();
    }

    @Override
    public TokenEntity getUserIdAndTypeByToken(String token) {
        return query().select("user_id", "type").eq("token", token).apply("expire_time > now()").last(Const.LIMIT_ONE).one();
    }

    @Override
    public Long getUserIdByToken(String token) {
        return query().select("user_id").eq("token", token).apply("expire_time > now()").last(Const.LIMIT_ONE).oneOpt().map(TokenEntity::getUserId).orElse(null);
    }

    @Override
    public boolean renewalToken(String token, Long expire) {
        return update().setSql("expire_time = DATE_ADD(NOW(), interval " + expire + " second)").eq("token", token).update(new TokenEntity());
    }

    @Override
    public boolean deleteToken(String token) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().eq("token", token));
    }

    @Override
    public boolean deleteTokenByUserId(Long userId, String type) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().eq("user_id", userId).eq("type", type));
    }

    @Override
    public boolean deleteByUserIds(List<Long> userIds) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().in("user_id", userIds));
    }

}

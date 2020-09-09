package com.nb6868.onex.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.service.impl.BaseServiceImpl;
import com.nb6868.onex.modules.uc.dao.TokenDao;
import com.nb6868.onex.modules.uc.dto.LoginChannelCfg;
import com.nb6868.onex.modules.uc.entity.TokenEntity;
import com.nb6868.onex.modules.uc.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * token
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class TokenServiceImpl extends BaseServiceImpl<TokenDao, TokenEntity> implements TokenService {

    @Override
    public String createToken(Long userId, LoginChannelCfg loginConfig) {
        // 当前时间
        Date now = new Date();
        // 过期时间
        Date expireTime = new Date(now.getTime() + loginConfig.getExpire() * 1000);
        // 生成的token
        if (loginConfig.isMultiLogin()) {
            // 支持多点登录
        } else {
            // 不支持多点登录,注销该用户所有token
            deleteTokenByUserId(userId, loginConfig.getType());
        }
        // 不管逻辑，永远都是重新生成一个token
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(expireTime);
        tokenEntity.setType(loginConfig.getType());

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
        // UPDATE uc_token SET expire_time = DATE_ADD(NOW(), interval #{expire} second) WHERE token = #{token} and deleted = 0
        return update().setSql("expire_time = DATE_ADD(NOW(), interval " + expire + " second)").eq("token", token).update(new TokenEntity());
    }

    @Override
    public boolean deleteToken(String token) {
        // 修改token
        getBaseMapper().deleteByWrapperWithFill(new TokenEntity(), new QueryWrapper<TokenEntity>().eq("token", token));
        return true;
    }

    @Override
    public boolean deleteTokenByUserId(Long userId, Integer type) {
        return update().set("deleted", 1).eq("user_id", userId).eq("type", type).update(new TokenEntity());
    }

}

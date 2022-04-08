package com.nb6868.onex.uc.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.common.util.TokenUtils;
import com.nb6868.onex.uc.dao.TokenDao;
import com.nb6868.onex.uc.entity.TokenEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.stereotype.Service;

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
     * @param loginConfig 登录配置
     * @return result
     */
    public String createToken(UserEntity user, AuthProps.Config loginConfig) {
        String jwtToken = TokenUtils.getToken(loginConfig, Dict.create().set("id", user.getId())
                .set("deptCode", user.getDeptCode())
                .set("areaCode", user.getAreaCode())
                .set("tenantCode", user.getTenantCode())
                .set("username", user.getUsername())
                .set("realName", user.getRealName()));
        if ("db".equalsIgnoreCase(loginConfig.getTokenStoreType())) {
            // 在数据库中
            if (!loginConfig.isMultiLogin()) {
                // 不支持多端登录,注销该用户所有token
                deleteTokenByUserId(user.getId(), loginConfig.getType());
            }
            // 不管逻辑，永远都是重新生成一个token
            TokenEntity tokenEntity = new TokenEntity();
            tokenEntity.setUserId(user.getId());
            tokenEntity.setTenantCode(user.getTenantCode());
            tokenEntity.setToken(jwtToken);
            tokenEntity.setType(loginConfig.getType());
            tokenEntity.setExpireTime(loginConfig.getTokenExpire() == -1 ? DateUtil.offsetMonth(new Date(), 12 * 99) : DateUtil.offsetSecond(new Date(), loginConfig.getTokenExpire()));
            // 保存token
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
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().eq("token", token));
    }

    /**
     * 删除用户下所有token
     *
     * @param userId 用户id
     * @param type   登录类型
     * @return result
     */
    public boolean deleteTokenByUserId(Long userId, String type) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>()
                .eq("user_id", userId)
                .eq(StrUtil.isNotBlank(type), "type", type));
    }

    /**
     * 删除用户token
     *
     * @param userIds 用户ID数组
     * @return result
     */
    public boolean deleteByUserIds(List<Long> userIds) {
        return logicDeleteByWrapper(new QueryWrapper<TokenEntity>().in("user_id", userIds));
    }

}

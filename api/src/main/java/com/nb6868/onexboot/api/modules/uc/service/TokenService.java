package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.common.service.BaseService;
import com.nb6868.onexboot.api.modules.uc.dto.LoginChannelCfg;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;

/**
 * 用户Token
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface TokenService extends BaseService<TokenEntity> {

    /**
     * 通过token获取用户id和登录type
     *
     * @return result
     */
    TokenEntity getUserIdAndTypeByToken(String token);

    /**
     * 通过token获取用户id
     *
     * @return result
     */
    Long getUserIdByToken(String token);

    /**
     * 生成token
     *
     * @param userId      用户ID
     * @param loginConfig 登录配置
     * @return result
     */
    String createToken(Long userId, LoginChannelCfg loginConfig);

    /**
     * token续期
     *
     * @param token  token
     * @param expire 延长时间
     * @return result
     */
    boolean renewalToken(String token, Long expire);

    /**
     * 注销token
     *
     * @param token 用户token
     * @return result
     */
    boolean deleteToken(String token);

    /**
     * 删除用户下所有token
     *
     * @param userId 用户id
     * @param type   登录类型
     * @return result
     */
    boolean deleteTokenByUserId(Long userId, Integer type);

}

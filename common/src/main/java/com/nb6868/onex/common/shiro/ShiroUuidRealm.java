package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.auth.AuthConst;
import com.nb6868.onex.common.params.BaseParamsService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ShiroUuidRealm
 * 表示token是uuid(随机生成的无含义数据，不一定uuid)，需要通过持久化存储(数据库/缓存)来验证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@ConditionalOnProperty(name = "onex.shiro.type", havingValue = "uuid", matchIfMissing = false)
public class ShiroUuidRealm extends BaseShiroRealm {

    @Autowired
    BaseParamsService paramsService;
    @Autowired
    ShiroDao shiroDao;

    /**
     * 认证(登录时调用)
     * doGetAuthenticationInfo->doGetAuthorizationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // AuthenticationToken包含身份信息和认证信息，在Filter中塞入
        String token = getTokenFromAuthenticationToken(authenticationToken);
        Assert.isTrue(StrUtil.isNotBlank(token), () -> new AuthenticationException(Const.MSG_LOGIN_REQUIRED));
        // token存在数据库/缓存中
        Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
        Assert.notNull(tokenEntity, () -> new AuthenticationException("登录信息已失效,请重新登录..."));
        // 检查账号id信息
        Long userId = MapUtil.getLong(tokenEntity, "user_id");
        Assert.notNull(userId, () -> new AuthenticationException("缺少登录用户信息,请重新登录..."));
        // 验证用户是否还存在
        Map<String, Object> userEntity = shiroDao.getUserById(userId);
        // 账号不存在
        Assert.notNull(userEntity, () -> new AuthenticationException("缺少登录账号信息,请重新登录..."));
        // 账号锁定
        Assert.isTrue(MapUtil.getInt(userEntity, "state", -1) == ShiroConst.USER_STATE_ENABLED, () -> new AuthenticationException("账号已锁定,请联系管理员..."));

        String loginType = MapUtil.getStr(tokenEntity, "type");
        // 获取jwt中的登录配置
        JSONObject loginConfig = paramsService.getSystemPropsJson(loginType);
        Assert.notNull(loginConfig, () -> new AuthenticationException("缺少登录信息配置,请重新登录..."));
        // 转换成UserDetail对象
        ShiroUser shiroUser = BeanUtil.toBean(userEntity, ShiroUser.class, CopyOptions.create().setAutoTransCamelCase(true).setIgnoreCase(true));
        shiroUser.setLoginType(loginType);
        shiroUser.setLoginConfig(loginConfig);
        // token续期
        if (loginConfig.getInt(AuthConst.TOKEN_RENEWAL_EXPIRE_KEY, AuthConst.TOKEN_RENEWAL_EXPIRE_VALUE) > 0) {
            shiroDao.updateTokenExpireTime(token, loginConfig.getInt(AuthConst.TOKEN_RENEWAL_EXPIRE_KEY, AuthConst.TOKEN_RENEWAL_EXPIRE_VALUE));
        }
        return new SimpleAuthenticationInfo(shiroUser, token, getName());
    }

}

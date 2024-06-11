package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.auth.AuthConst;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.validator.AssertUtils;
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
 *
 * 表示token是uuid(随机生成的无含义数据，不一定uuid)，需要通过持久化存储(数据库/缓存)来验证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@ConditionalOnProperty(name = "onex.shiro.type", havingValue = "uuid", matchIfMissing = false)
public class ShiroUuidRealm extends BaseShiroRealm {

    @Autowired
    private BaseParamsService paramsService;
    @Autowired
    private ShiroDao shiroDao;

    /**
     * 认证(登录时调用)
     * doGetAuthenticationInfo->doGetAuthorizationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // AuthenticationToken包含身份信息和认证信息，在Filter中塞入
        AssertUtils.isNull(authenticationToken.getCredentials(), ErrorCode.UNAUTHORIZED);
        String token = authenticationToken.getCredentials().toString();
        // 适配token中带有Bearer的情况
        token = StrUtil.removePrefix(StrUtil.removePrefix(token, "Bearer "), "bearer ");
        // token存在数据库/缓存中
        Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
        AssertUtils.isNull(tokenEntity, ErrorCode.UNAUTHORIZED);
        Long userId = MapUtil.getLong(tokenEntity, "user_id");
        AssertUtils.isNull(userId, ErrorCode.UNAUTHORIZED);
        String loginType = MapUtil.getStr(tokenEntity, "type");
        // 获取jwt中的登录配置
        JSONObject loginConfig = paramsService.getSystemPropsJson(loginType);
        AssertUtils.isNull(loginConfig, ErrorCode.UNAUTHORIZED);

        // 验证用户是否还存在
        Map<String, Object> userEntity = shiroDao.getUserById(userId);
        // 账号不存在
        AssertUtils.isNull(userEntity, ErrorCode.ACCOUNT_NOT_EXIST);
        // 账号锁定
        AssertUtils.isFalse(MapUtil.getInt(userEntity, "state", -1) == ShiroConst.USER_STATE_ENABLED, ErrorCode.ACCOUNT_LOCK);
        // 转换成UserDetail对象
        ShiroUser shiroUser = BeanUtil.toBean(userEntity, ShiroUser.class, CopyOptions.create().setAutoTransCamelCase(true).setIgnoreCase(true));
        shiroUser.setLoginType(loginType);
        // token续期
        if (loginConfig.getInt(AuthConst.TOKEN_RENEWAL_EXPIRE_KEY, AuthConst.TOKEN_RENEWAL_EXPIRE_VALUE) > 0) {
            shiroDao.updateTokenExpireTime(token, loginConfig.getInt(AuthConst.TOKEN_RENEWAL_EXPIRE_KEY, AuthConst.TOKEN_RENEWAL_EXPIRE_VALUE));
        }
        return new SimpleAuthenticationInfo(shiroUser, token, getName());
    }

}

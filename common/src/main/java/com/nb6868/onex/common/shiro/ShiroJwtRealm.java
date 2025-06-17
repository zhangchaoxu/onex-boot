package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.auth.AuthConst;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.util.JwtUtils;
import jakarta.validation.constraints.NotNull;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ShiroJwtRealm
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@ConditionalOnProperty(name = "onex.shiro.type", havingValue = "jwt", matchIfMissing = true)
public class ShiroJwtRealm extends BaseShiroRealm {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private BaseParamsService paramsService;
    @Autowired
    private ShiroDao shiroDao;

    /**
     * 认证(登录时调用)
     * doGetAuthenticationInfo->doGetAuthorizationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(@NotNull AuthenticationToken authenticationToken) throws AuthenticationException {
        // AuthenticationToken包含身份信息和认证信息，在Filter中塞入
        String token = getTokenFromAuthenticationToken(authenticationToken);
        Assert.isTrue(StrUtil.isNotBlank(token), ()-> new AuthenticationException(Const.MSG_LOGIN_REQUIRED));
        // 尝试解析为jwt
        JWT jwt = JwtUtils.parseToken(token);
        Assert.isTrue(ObjUtil.isNotNull(jwt) && ObjUtil.isNotNull(jwt.getPayload()) && ObjUtil.isNotNull(jwt.getPayload().getClaimsJson()), ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_TOKEN_ERROR));
        // 获取jwt中的登录配置
        String loginType = jwt.getPayload().getClaimsJson().getStr(authProps.getTokenJwtKey());
        Assert.isTrue(StrUtil.isNotBlank(loginType), ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_TOKEN_ERROR));
        JSONObject loginConfig = paramsService.getSystemPropsJson(loginType);
        Assert.notNull(loginConfig, ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_PARAMS_MISS));
        // 获取用户id
        Long userId;
        if ("db".equalsIgnoreCase(loginConfig.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE))) {
            // token存在数据库中
            Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
            userId = MapUtil.getLong(tokenEntity, "user_id");
        } else {
            // token没有持久化，直接用jwt验证
            Assert.isTrue(JwtUtils.verifyKeyAndExp(jwt, loginConfig.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE)), ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_EXPIRED));
            userId = NumberUtil.parseLong(jwt.getPayload().getClaimsJson().getStr("id"));
        }
        Assert.notNull(userId, ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_USER_MISS));
        // 验证用户是否还存在
        Map<String, Object> userEntity = shiroDao.getUserById(userId);
        // 账号不存在
        Assert.notNull(userEntity, ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_USER_MISS));
        // 账号锁定
        Assert.isTrue(MapUtil.getInt(userEntity, "state", -1) == ShiroConst.USER_STATE_ENABLED, ()-> new AuthenticationException(ShiroConst.MSG_LOGIN_USER_LOCKED));
        // 转换成UserDetail对象,setIgnoreError保证过程不出错，但可能会吞掉异常问题
        ShiroUser shiroUser = BeanUtil.toBean(userEntity, ShiroUser.class, CopyOptions.create()
                .setAutoTransCamelCase(true)
                .setIgnoreCase(true)
                .setIgnoreError(true));
        if (ObjectUtil.isNotEmpty(userEntity.get("ext_info"))) {
            shiroUser.setExtInfo(JSONUtil.parseObj(userEntity.get("ext_info").toString()));
        }
        // 不要让ext_info为空
        if (shiroUser.getExtInfo() == null) {
            shiroUser.setExtInfo(new JSONObject());
        }
        shiroUser.setLoginType(loginType);
        shiroUser.setLoginConfig(loginConfig);
        // token续期
        if ("db".equalsIgnoreCase(loginConfig.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE)) && loginConfig.getInt(AuthConst.TOKEN_RENEWAL_EXPIRE_KEY, AuthConst.TOKEN_RENEWAL_EXPIRE_VALUE) > 0) {
            shiroDao.updateTokenExpireTime(token, loginConfig.getInt(AuthConst.TOKEN_RENEWAL_EXPIRE_KEY, AuthConst.TOKEN_RENEWAL_EXPIRE_VALUE));
        }
        return new SimpleAuthenticationInfo(shiroUser, token, getName());
    }

}

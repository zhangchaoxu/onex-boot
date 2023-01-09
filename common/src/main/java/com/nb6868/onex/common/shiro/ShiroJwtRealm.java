package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JwtUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * ShiroJwtRealm
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@ConditionalOnProperty(name = "onex.shiro.type", havingValue = "jwt")
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
        AssertUtils.isNull(authenticationToken.getCredentials(), ErrorCode.UNAUTHORIZED);
        String token = authenticationToken.getCredentials().toString();
        // 尝试解析为jwt
        JWT jwt = JwtUtils.parseToken(token);
        AssertUtils.isTrue(jwt == null || jwt.getPayload() == null || jwt.getPayload().getClaimsJson() == null, ErrorCode.UNAUTHORIZED);
        // 获取jwt中的登录配置
        String loginType = jwt.getPayload().getClaimsJson().getStr(authProps.getTokenJwtKey());
        AssertUtils.isEmpty(loginType, ErrorCode.UNAUTHORIZED);

        JSONObject loginConfig = paramsService.getSystemPropsJson(loginType);
        AssertUtils.isNull(loginConfig, ErrorCode.UNAUTHORIZED);

        // 获取用户id
        Long userId;
        if ("db".equalsIgnoreCase(loginConfig.getStr("tokenStoreType"))) {
            // token存在数据库中
            Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
            AssertUtils.isNull(tokenEntity, ErrorCode.UNAUTHORIZED);
            userId = MapUtil.getLong(tokenEntity, "user_id");
        } else {
            // token没有持久化，直接用jwt验证
            AssertUtils.isFalse(jwt.setKey(loginConfig.getStr("tokenKey", Const.TOKEN_KEY).getBytes()).validate(0), ErrorCode.UNAUTHORIZED);
            userId = jwt.getPayload().getClaimsJson().getLong("id");
        }
        AssertUtils.isNull(userId, ErrorCode.UNAUTHORIZED);
        // 验证用户是否还存在
        Map<String, Object> userEntity = shiroDao.getUserById(userId);
        // 账号不存在
        AssertUtils.isNull(userEntity, ErrorCode.ACCOUNT_NOT_EXIST);
        // 账号锁定
        AssertUtils.isFalse(MapUtil.getInt(userEntity, "state", -1) == ShiroConst.USER_STATE_ENABLED, ErrorCode.ACCOUNT_LOCK);
        // 转换成UserDetail对象
        ShiroUser shiroUser = BeanUtil.mapToBean(userEntity, ShiroUser.class, true, CopyOptions.create().setIgnoreCase(true));
        shiroUser.setLoginType(loginType);
        shiroUser.setLoginConfig(loginConfig);

        if ("db".equalsIgnoreCase(loginConfig.getStr("tokenStoreType")) && loginConfig.getBool("tokenRenewal", false)) {
            // 更新token
            shiroDao.updateTokenExpireTime(token, loginConfig.getInt("tokenExpire", 604800));
        }
        return new SimpleAuthenticationInfo(shiroUser, token, getName());
    }

}

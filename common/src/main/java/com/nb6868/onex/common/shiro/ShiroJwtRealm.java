package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthConst;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.util.JwtUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
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
        // 适配token中带有Bearer的情况
        token = StrUtil.removePrefix(StrUtil.removePrefix(token, "Bearer "), "bearer ");
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
        if ("db".equalsIgnoreCase(loginConfig.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE))) {
            // token存在数据库中
            Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
            AssertUtils.isNull(tokenEntity, ErrorCode.UNAUTHORIZED);
            userId = MapUtil.getLong(tokenEntity, "user_id");
        } else {
            // token没有持久化，直接用jwt验证
            AssertUtils.isFalse(JwtUtils.verifyKeyAndExp(jwt, loginConfig.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE)), ErrorCode.UNAUTHORIZED);
            userId = NumberUtil.parseLong(jwt.getPayload().getClaimsJson().getStr("id"));
        }
        AssertUtils.isNull(userId, ErrorCode.UNAUTHORIZED);
        // 验证用户是否还存在
        Map<String, Object> userEntity = shiroDao.getUserById(userId);
        // 账号不存在
        AssertUtils.isNull(userEntity, ErrorCode.ACCOUNT_NOT_EXIST);
        // 账号锁定
        AssertUtils.isFalse(MapUtil.getInt(userEntity, "state", -1) == ShiroConst.USER_STATE_ENABLED, ErrorCode.ACCOUNT_LOCK);
        // 转换成UserDetail对象,setIgnoreError保证过程不出错，但可能会吞掉异常问题
        ShiroUser shiroUser = BeanUtil.mapToBean(userEntity, ShiroUser.class, true, CopyOptions.create().setIgnoreCase(true).setIgnoreError(true));
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

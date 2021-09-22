package com.nb6868.onex.shop.shiro;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.JwtUtils;
import com.nb6868.onex.common.util.MessageUtils;
import com.nb6868.onex.shop.modules.uc.UcConst;
import com.nb6868.onex.shop.modules.uc.entity.TokenEntity;
import com.nb6868.onex.shop.modules.uc.entity.UserEntity;
import com.nb6868.onex.shop.modules.uc.service.AuthService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Shiro认证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private AuthService authService;
    @Autowired
    private AuthProps authProps;

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null;
    }

    /**
     * 认证(登录时调用)
     * doGetAuthenticationInfo->doGetAuthorizationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // AuthenticationToken包含身份信息和认证信息
        String token = authenticationToken.getCredentials().toString();
        JSONObject tokenClaimsJson = (JSONObject) authenticationToken.getPrincipal();
        AuthProps.Config loginConfig = authProps.getConfigs().get(tokenClaimsJson.getStr(authProps.getTokenTypeKey()));
        if (null == loginConfig || "full".equalsIgnoreCase(loginConfig.getVerifyType())) {
            // 完整校验,从数据库走
            // 根据accessToken，查询用户信息
            TokenEntity tokenEntity = authService.getUserTokenByToken(token);
            // token失效
            if (tokenEntity == null) {
                throw new IncorrectCredentialsException(MessageUtils.getMessage(ErrorCode.TOKEN_INVALID));
            }
            // 查询用户信息
            UserEntity userEntity = authService.getUser(tokenEntity.getUserId());

            if (userEntity == null) {
                // 账号不存在
                throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (userEntity.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                // 账号锁定
                throw new OnexException(ErrorCode.ACCOUNT_LOCK);
            }

            // 转换成UserDetail对象
            UserDetail userDetail = ConvertUtils.sourceToTarget(userEntity, UserDetail.class);

            userDetail.setLoginConfig(loginConfig);
            if (loginConfig != null && loginConfig.isTokenRenewal()) {
                // 更新token
                authService.updateTokenExpireTime(token, loginConfig.getTokenExpire());
            }
            return new SimpleAuthenticationInfo(userDetail, token, getName());
        } else if ("jwt".equalsIgnoreCase(loginConfig.getVerifyType()))  {
            // 校验token合法性
            if (!JwtUtils.verifyKey(token, loginConfig.getTokenKey().getBytes())) {
                throw new IncorrectCredentialsException(MessageUtils.getMessage(ErrorCode.TOKEN_INVALID));
            }
            // 查询用户信息
            UserEntity userEntity = authService.getUser(tokenClaimsJson.getLong("id"));
            if (userEntity == null) {
                // 账号不存在
                throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (userEntity.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                // 账号锁定
                throw new OnexException(ErrorCode.ACCOUNT_LOCK);
            }
            // 转换成UserDetail对象
            UserDetail userDetail = ConvertUtils.sourceToTarget(userEntity, UserDetail.class);
            userDetail.setLoginConfig(loginConfig);
            if (loginConfig.isTokenRenewal()) {
                // 更新token
                authService.updateTokenExpireTime(token, loginConfig.getTokenExpire());
            }
            return new SimpleAuthenticationInfo(userDetail, token, getName());
        } else {
            // 校验token合法性
            if (!JwtUtils.verifyKey(token, loginConfig.getTokenKey().getBytes())) {
                throw new IncorrectCredentialsException(MessageUtils.getMessage(ErrorCode.TOKEN_INVALID));
            }
            // 只校验token
            // 转换成UserDetail对象
            UserDetail userDetail = JSONUtil.toBean(tokenClaimsJson, UserDetail.class);
            userDetail.setLoginConfig(loginConfig);
            return new SimpleAuthenticationInfo(userDetail, token, getName());
        }
    }

    /**
     * 授权(验证权限时调用)
     * 验证token不会过这个方法
     * 只有当需要检测用户权限的时候才会调用此方法,例如RequiresPermissions/checkRole/checkPermission
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserDetail user = (UserDetail) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 根据配置中的role和permission设置SimpleAuthorizationInfo
        if (null != user.getLoginConfig() && user.getLoginConfig().isRoleBase()) {
            // 塞入角色列表
            info.setRoles(authService.getUserRoles(user));
        }
        if (null != user.getLoginConfig() && user.getLoginConfig().isPermissionBase()) {
            // 塞入权限列表
            info.setStringPermissions(authService.getUserPermissions(user));
        }
        return info;
    }

}

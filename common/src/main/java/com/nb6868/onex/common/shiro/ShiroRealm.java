package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shiro认证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private ShiroDao shiroDao;

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
        AuthProps.Config loginConfig;
        // 尝试解析为jwt
        JWT jwt = JwtUtils.parseToken(token);
        if (jwt == null) {
            // 非jwt,从数据库里获得类型
            Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
            if (tokenEntity == null) {
                throw new OnexException(ErrorCode.UNAUTHORIZED);
            }
            loginConfig = authProps.getConfigs().get(MapUtil.getStr(tokenEntity, "type"));
            if (null == loginConfig) {
                throw new OnexException(ErrorCode.UNAUTHORIZED);
            }
            // 查询用户信息
            Map<String, Object> userEntity = shiroDao.getUserById(MapUtil.getLong(tokenEntity, "user_id"));
            if (userEntity == null) {
                // 账号不存在
                throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (MapUtil.getInt(userEntity, "state") != ShiroConst.USER_STATE_ENABLED) {
                // 账号锁定
                throw new OnexException(ErrorCode.ACCOUNT_LOCK);
            }
            // 转换成UserDetail对象
            ShiroUser userDetail = BeanUtil.mapToBean(userEntity, ShiroUser.class, true, CopyOptions.create().setIgnoreCase(true));
            userDetail.setLoginConfig(loginConfig);
            if (loginConfig.isTokenRenewal()) {
                // 更新token
                shiroDao.updateTokenExpireTime(token, loginConfig.getTokenExpire());
            }
            return new SimpleAuthenticationInfo(userDetail, token, getName());
        } else {
            // jwt,先验证
            loginConfig = authProps.getConfigs().get(jwt.getPayload().getClaimsJson().getStr(authProps.getTokenTypeKey()));
            if (null == loginConfig || !JwtUtils.verifyKey(jwt, loginConfig.getTokenKey())) {
                throw new OnexException(ErrorCode.UNAUTHORIZED);
            }
            // 开始验证数据
            if ("jwt".equalsIgnoreCase(loginConfig.getVerifyType()))  {
                // jwt校验
                Map<String, Object> userEntity = shiroDao.getUserById(jwt.getPayload().getClaimsJson().getLong("id"));
                if (userEntity == null) {
                    // 账号不存在
                    throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
                } else if (MapUtil.getInt(userEntity, "state") != ShiroConst.USER_STATE_ENABLED) {
                    // 账号锁定
                    throw new OnexException(ErrorCode.ACCOUNT_LOCK);
                }
                // 转换成UserDetail对象
                ShiroUser userDetail = BeanUtil.mapToBean(userEntity, ShiroUser.class, true, CopyOptions.create().setIgnoreCase(true));
                userDetail.setLoginConfig(loginConfig);
                if (loginConfig.isTokenRenewal()) {
                    // 更新token
                    shiroDao.updateTokenExpireTime(token, loginConfig.getTokenExpire());
                }
                return new SimpleAuthenticationInfo(userDetail, token, getName());
            } else if ("jwtSimple".equalsIgnoreCase(loginConfig.getVerifyType()))  {
                // 只简单校验jwt密钥和有效时间,不过数据库
                if (!JwtUtils.verifyKeyAndExp(jwt, loginConfig.getTokenKey())) {
                    throw new OnexException(ErrorCode.UNAUTHORIZED);
                }
                // 转换成UserDetail对象
                ShiroUser userDetail = JSONUtil.toBean(jwt.getPayload().getClaimsJson(), ShiroUser.class);
                userDetail.setLoginConfig(loginConfig);
                return new SimpleAuthenticationInfo(userDetail, token, getName());
            } else {
                // 默认full,完整校验,从数据库走
                // 根据accessToken，查询用户信息
                Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
                if (tokenEntity == null) {
                    throw new OnexException(ErrorCode.UNAUTHORIZED);
                }
                // 查询用户信息
                Map<String, Object> userEntity = shiroDao.getUserById(MapUtil.getLong(tokenEntity, "user_id"));
                if (userEntity == null) {
                    // 账号不存在
                    throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
                } else if (MapUtil.getInt(userEntity, "state") != ShiroConst.USER_STATE_ENABLED) {
                    // 账号锁定
                    throw new OnexException(ErrorCode.ACCOUNT_LOCK);
                }
                // 转换成UserDetail对象
                ShiroUser userDetail = BeanUtil.mapToBean(userEntity, ShiroUser.class, true, CopyOptions.create().setIgnoreCase(true));
                userDetail.setLoginConfig(loginConfig);
                if (loginConfig.isTokenRenewal()) {
                    // 更新token
                    shiroDao.updateTokenExpireTime(token, loginConfig.getTokenExpire());
                }
                return new SimpleAuthenticationInfo(userDetail, token, getName());
            }
        }
    }

    /**
     * 授权(验证权限时调用)
     * 验证token不会过这个方法
     * 只有当需要检测用户权限的时候才会调用此方法,例如RequiresPermissions/checkRole/checkPermission
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser user = (ShiroUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 根据配置中的role和permission设置SimpleAuthorizationInfo
        if (null != user.getLoginConfig() && user.getLoginConfig().isPermissionBase()) {
            // 塞入角色列表,超级管理员全部
            List<String> permissionsList = user.isFullPermissions() ? shiroDao.getAllPermissionsList() : shiroDao.getPermissionsListByUserId(user.getId());
            Set<String> set = new HashSet<>();
            permissionsList.forEach(permissions -> set.addAll(StrUtil.splitTrim(permissions, ",")));
            info.setStringPermissions(set);
        }
        if (null != user.getLoginConfig() && user.getLoginConfig().isRoleBase()) {
            // 塞入权限列表,超级管理员全部
            info.setRoles(new HashSet<>(user.isFullRoles() ? shiroDao.getAllRoleCodeList() : shiroDao.getRoleCodeListByUserId(user.getId())));
        }
        return info;
    }

}

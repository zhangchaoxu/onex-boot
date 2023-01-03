package com.nb6868.onex.common.shiro;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.util.JwtUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ShiroJwtRealm
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@ConditionalOnProperty(name = "onex.shiro.type", havingValue = "jwt")
public class ShiroJwtRealm extends AuthorizingRealm {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private BaseParamsService paramsService;
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

    /**
     * 授权(验证权限时调用)
     * 验证token不会过这个方法
     * 只有当需要检测用户权限的时候才会调用此方法
     * 例如RequiresPermissions/checkRole/checkPermission
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 根据配置中的role和permission设置SimpleAuthorizationInfo
        Set<String> permissionSet = new HashSet<>();
        if (null != shiroUser.getLoginConfig() && shiroUser.getLoginConfig().getBool("permissionBase", true)) {
            // 塞入角色列表,超级管理员全部
            List<String> permissionsList = shiroUser.isFullPermissions() ? shiroDao.getAllPermissionsList(shiroUser.getTenantCode()) : shiroDao.getPermissionsListByUserId(shiroUser.getId());
            permissionsList.forEach(permissions -> permissionSet.addAll(StrUtil.splitTrim(permissions, ",")));
        }
        // 超级管理员，加入角色权限
        if (shiroUser.isFullRoles()) {
            permissionSet.add("admin:super");
        }
        info.setStringPermissions(permissionSet);
        if (null != shiroUser.getLoginConfig() && shiroUser.getLoginConfig().getBool("roleBase", false)) {
            // 塞入权限列表,超级管理员全部
            List<Long> roleList = shiroUser.isFullRoles() ? shiroDao.getAllRoleIdList(shiroUser.getTenantCode()) : shiroDao.getRoleIdListByUserId(shiroUser.getId());
            Set<String> set = new HashSet<>();
            roleList.forEach(aLong -> set.add(String.valueOf(aLong)));
            info.setRoles(set);
        }
        return info;
    }

}

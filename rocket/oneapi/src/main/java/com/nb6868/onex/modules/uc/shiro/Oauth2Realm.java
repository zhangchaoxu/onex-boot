package com.nb6868.onex.modules.uc.shiro;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.util.ConvertUtils;
import com.nb6868.onex.booster.util.MessageUtils;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.dto.LoginChannelCfg;
import com.nb6868.onex.modules.uc.entity.TokenEntity;
import com.nb6868.onex.modules.uc.entity.UserEntity;
import com.nb6868.onex.modules.uc.service.ShiroService;
import com.nb6868.onex.modules.uc.user.UserDetail;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nb6868.onex.modules.uc.UcConst.TOKEN_ANON;
import static com.nb6868.onex.modules.uc.UcConst.TOKEN_GUEST;

/**
 * 认证
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Component
public class Oauth2Realm extends AuthorizingRealm {

    @Autowired
    private ShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserDetail user = (UserDetail) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (TOKEN_ANON.equalsIgnoreCase(user.getToken())) {
            // 匿名用户
            Set<String> roles = new HashSet<>();
            roles.add(UcConst.ROLE_CODE_ANON);
            info.setRoles(roles);
        } else if (UcConst.TOKEN_GUEST.equalsIgnoreCase(user.getToken())) {
            // 游客token,不做验证
            // 塞入游客角色
            Set<String> roles = new HashSet<>();
            roles.add(UcConst.ROLE_CODE_GUEST);
            info.setRoles(roles);
            // 塞入游客具有的权限列表
            List<String> roleCodes = new ArrayList<>();
            roleCodes.add(UcConst.ROLE_CODE_GUEST);
            info.setStringPermissions(shiroService.getPermissionsByRoles(roleCodes));
        } else {
            // 根据登录配置中的roleBase和permissionBase设置SimpleAuthorizationInfo
            LoginChannelCfg loginCfg = user.getLoginCfg();
            if (loginCfg != null) {
                if (loginCfg.isRoleBase()) {
                    // 塞入角色列表
                    info.setRoles(shiroService.getUserRoleCodes(user));
                }
                if (loginCfg.isPermissionsBase()) {
                    // 塞入权限列表
                    info.setStringPermissions(shiroService.getUserPermissions(user));
                }
            }
        }
        return info;
    }

    /**
     * 认证(登录时调用)
     * 先doGetAuthenticationInfo,再doGetAuthorizationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        String accessToken = (String) authToken.getPrincipal();
        if (TOKEN_ANON.equalsIgnoreCase(accessToken)) {
            // 匿名访问
            UserDetail userDetail = new UserDetail();
            userDetail.setToken(TOKEN_ANON);
            userDetail.setType(-100);
            return new SimpleAuthenticationInfo(userDetail, TOKEN_ANON, getName());
        } else if (TOKEN_GUEST.equalsIgnoreCase(accessToken)) {
            // 游客访问
            UserDetail userDetail = new UserDetail();
            userDetail.setToken(TOKEN_GUEST);
            userDetail.setType(-100);
            return new SimpleAuthenticationInfo(userDetail, TOKEN_GUEST, getName());
        }
        // 根据accessToken，查询用户信息
        TokenEntity token = shiroService.getUserIdAndTypeByToken(accessToken);
        // token失效
        if (token == null) {
            throw new IncorrectCredentialsException(MessageUtils.getMessage(ErrorCode.TOKEN_INVALID));
        }

        // 查询用户信息
        UserEntity userEntity = shiroService.getUser(token.getUserId());

        if (userEntity == null) {
            // 账号不存在
            throw new OnexException(MessageUtils.getMessage(ErrorCode.ACCOUNT_NOT_EXIST));
        } else if (userEntity.getStatus() != UcConst.UserStatusEnum.ENABLED.value()) {
            // 账号锁定
            throw new LockedAccountException(MessageUtils.getMessage(ErrorCode.ACCOUNT_LOCK));
        }

        // 转换成UserDetail对象
        UserDetail userDetail = ConvertUtils.sourceToTarget(userEntity, UserDetail.class);

        // 将登录配置塞入user
        LoginChannelCfg loginCfg = shiroService.getLoginCfg(token.getType());
        userDetail.setLoginCfg(loginCfg);

        // 将token塞入user
        userDetail.setToken(accessToken);

        // 获取用户对应的部门数据权限
        /*List<Long> deptIdList = shiroService.getDataScopeList(userDetail.getId());
        userDetail.setDeptIdList(deptIdList);*/

        // 更新token
        if (loginCfg.isRenewalToken()) {
            shiroService.renewalToken(accessToken, loginCfg.getExpire());
        }

        return new SimpleAuthenticationInfo(userDetail, accessToken, getName());
    }

}

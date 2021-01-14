package com.nb6868.onexboot.api.modules.uc.shiro;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.service.ShiroService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.MessageUtils;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Shiro认证
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private ShiroService shiroService;

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null;
    }

    /**
     * shiro是否基于角色或者权限来判断
     */
    @Value("${onex.shiro.role: false}")
    private boolean shiroRole;
    @Value("${onex.shiro.permissions: true}")
    private boolean shiroPermissions;
    @Value("${onex.shiro.token.renewal: true}")
    private boolean tokenRenewal;
    @Value("${onex.shiro.token.expire: 604800}")
    private long tokenExpire;

    /**
     * 授权(验证权限时调用)
     * 只有当需要检测用户权限的时候才会调用此方法,例如RequiresPermissions/checkRole/checkPermission
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserDetail user = (UserDetail) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (user.isAnon()) {
            // 匿名用户
            Set<String> roles = new HashSet<>();
            roles.add(UcConst.ROLE_CODE_ANON);
            info.setRoles(roles);
        } else {
            // 根据配置文件中的role和permission设置SimpleAuthorizationInfo
            if (shiroRole) {
                // 塞入角色列表
                info.setRoles(shiroService.getUserRoles(user));
            }
            if (shiroPermissions) {
                // 塞入权限列表
                info.setStringPermissions(shiroService.getUserPermissions(user));
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
        if (UcConst.TOKEN_ANON.equalsIgnoreCase(accessToken)) {
            // 匿名访问
            UserDetail userDetail = new UserDetail();
            userDetail.setId(-1L);
            userDetail.setType(-100);
            return new SimpleAuthenticationInfo(userDetail, UcConst.TOKEN_ANON, getName());
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

        // 获取用户对应的部门数据权限
        /*List<Long> deptIdList = shiroService.getDataScopeList(userDetail.getId());
        userDetail.setDeptIdList(deptIdList);*/

        // 更新token
        if (tokenRenewal) {
            shiroService.renewalToken(accessToken, tokenExpire);
        }

        return new SimpleAuthenticationInfo(userDetail, accessToken, getName());
    }

}

package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.dto.LoginChannelCfg;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * shiro
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ParamService paramService;
    @Autowired
    private RoleDataScopeService roleDataScopeService;

    @Value("${redis.open: false}")
    private boolean open;

    @Override
    public Set<String> getPermissionsByRoles(List<String> roleCodes) {
        List<String> permissionsList;
        permissionsList = menuService.getPermissionsListByRoles(roleCodes);
        // 用户权限列表
        Set<String> set = new HashSet<>();
        for (String permissions : permissionsList) {
            if (StringUtils.isBlank(permissions)) {
                continue;
            }
            set.addAll(StringUtils.splitToList(permissions));
        }

        return set;
    }

    @Override
    public Set<String> getUserPermissions(UserDetail user) {
        // 系统管理员，拥有最高权限
        List<String> permissionsList;
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            permissionsList = menuService.getPermissionsList();
        } else {
            permissionsList = menuService.getPermissionsListByUserId(user.getId());
        }

        // 用户权限列表
        Set<String> set = new HashSet<>();
        for (String permissions : permissionsList) {
            if (StringUtils.isBlank(permissions)) {
                continue;
            }
            set.addAll(StringUtils.splitToList(permissions));
        }

        return set;
    }

    @Override
    public Set<String> getUserRoleCodes(UserDetail user) {
        List<String> roleList;
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            roleList = roleService.getRoleCodeList();
        } else {
            roleList = roleService.getRoleCodeListByUserId(user.getId());
        }

        // 用户角色列表
        Set<String> set = new HashSet<>();
        for (String role : roleList) {
            if (StringUtils.isBlank(role)) {
                continue;
            }
            set.addAll(StringUtils.splitToList(role));
        }

        return set;
    }

    @Override
    public TokenEntity getUserIdAndTypeByToken(String token) {
        return tokenService.getUserIdAndTypeByToken(token);
    }

    @Override
    public UserEntity getUser(Long userId) {
        return userService.getById(userId);
    }

    @Override
    public List<Long> getDeptIdListByUserId(Long userId) {
        return roleDataScopeService.getDeptIdListByUserId(userId);
    }

    @Override
    public LoginChannelCfg getLoginCfg(Integer type) {
        return paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + type, LoginChannelCfg.class);
    }

    @Override
    public boolean renewalToken(String token, Long expire) {
        return tokenService.renewalToken(token, expire);
    }

}

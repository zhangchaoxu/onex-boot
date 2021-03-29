package com.nb6868.onexboot.api.modules.uc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * shiro相关接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ShiroService {

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
    private MenuScopeService menuScopeService;

    @Value("${redis.open: false}")
    private boolean open;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(UserDetail user) {
        // 系统管理员，拥有最高权限
        List<String> permissionsList;
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            permissionsList = menuService.listObjs(new QueryWrapper<MenuEntity>().select("permissions").ne("permissions", ""), Object::toString);
        } else {
            permissionsList = menuScopeService.getPermissionsListByUserId(user.getId());
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

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(UserDetail user) {
        List<Long> roleList = user.getType() == UcConst.UserTypeEnum.ADMIN.value() ? roleService.getRoleIdList() : roleService.getRoleIdListByUserId(user.getId());
        // 用户角色列表
        Set<String> set = new HashSet<>();
        for (Long role : roleList) {
            set.add(String.valueOf(role));
        }
        return set;
    }

    /**
     * 通过token获取用户id
     */
    public TokenEntity getUserIdAndTypeByToken(String token) {
        return tokenService.getUserIdAndTypeByToken(token);
    }

    /**
     * 续token的过期时间
     *
     * @param token  token
     * @param expire 续期时间
     */
    public boolean renewalToken(String token, Long expire) {
        return tokenService.renewalToken(token, expire);
    }

    /**
     * 根据用户ID，查询用户
     *
     * @param userId 用户id
     */
    public UserEntity getUser(Long userId) {
        return userService.getById(userId);
    }

}

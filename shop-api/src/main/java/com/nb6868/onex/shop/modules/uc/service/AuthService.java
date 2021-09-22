package com.nb6868.onex.shop.modules.uc.service;

import cn.hutool.core.text.StrSplitter;
import com.nb6868.onex.shop.modules.uc.dao.AuthDao;
import com.nb6868.onex.shop.modules.uc.entity.TokenEntity;
import com.nb6868.onex.shop.modules.uc.entity.UserEntity;
import com.nb6868.onex.shop.shiro.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 登录授权相关服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class AuthService {

    @Autowired
    AuthDao authDao;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(UserDetail user) {
        // 系统管理员，拥有最高权限
        List<String> permissionsList = authDao.getPermissionsListByUserId(user.getId());

        // 用户权限列表
        Set<String> set = new HashSet<>();
        permissionsList.forEach(permissions -> {
            set.addAll(StrSplitter.splitTrim(permissions, ',', true));
        });
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(UserDetail user) {
        List<Long> roleList = authDao.getUserRolesByUserId(user.getId());
        // 用户角色列表
        Set<String> set = new HashSet<>();
        for (Long role : roleList) {
            set.add(String.valueOf(role));
        }
        return set;
    }

    /**
     * 根据用户ID，查询用户
     *
     * @param userId 用户id
     */
    public UserEntity getUser(Long userId) {
        return authDao.getUserById(userId);
    }

    /**
     * 通过token获取用户id
     */
    public TokenEntity getUserTokenByToken(String token) {
        return authDao.getUserTokenByToken(token);
    }

    /**
     * 通过token获取用户id
     */
    public boolean updateTokenExpireTime(String token, Integer expireTime) {
        return authDao.updateTokenExpireTime(token, expireTime);
    }

}

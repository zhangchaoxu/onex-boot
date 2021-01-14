package com.nb6868.onexboot.api.modules.uc.service;

import com.nb6868.onexboot.api.modules.uc.dto.LoginChannelCfg;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;

import java.util.List;
import java.util.Set;

/**
 * shiro相关接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface ShiroService {

    /**
     * 通过角色获取权限列表
     */
    Set<String> getPermissionsByRoles(List<String> roleCodes);

    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(UserDetail user);

    /**
     * 获取用户角色列表
     */
    Set<String> getUserRoles(UserDetail user);

    /**
     * 通过token获取用户id
     */
    TokenEntity getUserIdAndTypeByToken(String token);

    /**
     * 续token的过期时间
     * @param token
     * @param expire
     */
    boolean renewalToken(String token, Long expire);

    /**
     * 获得登录配置
     * @param type
     * @return
     */
    LoginChannelCfg getLoginCfg(Integer type);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    UserEntity getUser(Long userId);

    /**
     * 获取用户对应的部门数据权限
     * @param userId  用户ID
     * @return        返回部门ID列表
     */
    List<Long> getDeptIdListByUserId(Long userId);

}

package com.nb6868.onex.uc.service;

import cn.hutool.core.text.StrSplitter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.common.shiro.ShiroDao;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.auth.LoginForm;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.TokenEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private AuthProps loginProps;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ShiroDao shiroDao;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(ShiroUser user) {
        List<String> permissionsList = user.isFullPermissions() ? shiroDao.getAllPermissionsList(user.getTenantCode()) : shiroDao.getPermissionsListByUserId(user.getId());
        Set<String> set = new HashSet<>();
        permissionsList.forEach(permissions -> set.addAll(StrSplitter.splitTrim(permissions, ',', true)));
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(ShiroUser user) {
        List<String> roleList = user.isFullRoles() ? shiroDao.getAllRoleCodeList(user.getTenantCode()) : shiroDao.getRoleCodeListByUserId(user.getId());
        // 用户角色列表
        return new HashSet<>(roleList);
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
    public boolean renewalToken(String token, Integer expire) {
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

    public AuthProps.Config getLoginConfig(String type) {
        return loginProps.getConfigs().get(type);
    }

    public AuthProps.Settings getLoginSettings(String type) {
        return loginProps.getSettings().get(type);
    }

    public UserEntity login(LoginForm loginRequest, AuthProps.Config loginProps) {
        // 校验验证码
        if (loginProps.isCaptcha()) {
            ValidatorUtils.validateEntity(loginRequest, LoginForm.CaptchaGroup.class);
            boolean validateCaptcha = loginRequest.getCaptchaValue().equalsIgnoreCase(loginProps.getMagicCaptcha()) || captchaService.validate(loginRequest.getCaptchaUuid(), loginRequest.getCaptchaValue());
            AssertUtils.isFalse(validateCaptcha, ErrorCode.CAPTCHA_ERROR);
        }

        // 登录用户
        UserEntity user;
        if (loginRequest.getAuthConfigType().endsWith("USERNAME_PASSWORD")) {
            // 帐号密码登录
            ValidatorUtils.validateEntity(loginRequest, LoginForm.UsernamePasswordGroup.class);
            user = userService.getOneByColumn("username", loginRequest.getUsername());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
            AssertUtils.isFalse(PasswordUtils.verify(loginRequest.getPassword(), user.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }  else {
            throw new OnexException(ErrorCode.UNKNOWN_LOGIN_TYPE);
        }

        // 登录成功
        return user;
    }

}

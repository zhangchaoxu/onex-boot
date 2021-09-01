package com.nb6868.onex.api.modules.uc.service;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onex.api.modules.msg.MsgConst;
import com.nb6868.onex.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onex.api.modules.msg.service.MailLogService;
import com.nb6868.onex.api.modules.sys.service.ParamService;
import com.nb6868.onex.api.modules.uc.UcConst;
import com.nb6868.onex.api.modules.uc.dto.LoginRequest;
import com.nb6868.onex.api.modules.uc.entity.MenuEntity;
import com.nb6868.onex.api.modules.uc.entity.TokenEntity;
import com.nb6868.onex.api.modules.uc.entity.UserEntity;
import com.nb6868.onex.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onex.api.modules.uc.user.UserDetail;
import com.nb6868.onex.common.auth.LoginProps;
import com.nb6868.onex.common.dingtalk.DingtalkScanProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
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
    private LoginProps loginProps;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOauthService userOauthService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MenuScopeService menuScopeService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private MailLogService mailLogService;

    /**
     * 获取用户权限列表
     */
    public Set<String> getUserPermissions(UserDetail user) {
        // 系统管理员，拥有最高权限
        List<String> permissionsList = user.getType() == UcConst.UserTypeEnum.ADMIN.value() ? menuService.listObjs(new QueryWrapper<MenuEntity>().select("permissions")
                .ne("permissions", "").isNotNull("permissions"), Object::toString) :
                menuScopeService.getPermissionsListByUserId(user.getId());

        // 用户权限列表
        Set<String> set = new HashSet<>();
        permissionsList.forEach(permissions -> set.addAll(StrSplitter.splitTrim(permissions, ',', true)));
        return set;
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getUserRoles(UserDetail user) {
        List<Long> roleList = user.getType() == UcConst.UserTypeEnum.ADMIN.value() ? roleService.getRoleIdList() : roleService.getRoleIdListByUserId(user.getId());
        // 用户角色列表
        Set<String> set = new HashSet<>();
        roleList.forEach(role -> set.add(String.valueOf(role)));
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

    public LoginProps.Config getLoginConfig(String type) {
        return loginProps.getConfigs().get(type);
    }

    public LoginProps.Settings getLoginSettings(String type) {
        return loginProps.getSettings().get(type);
    }

    public UserEntity login(LoginRequest loginRequest, LoginProps.Config loginProps) {
        // 校验验证码
        if (loginProps.isCaptcha()) {
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.CaptchaGroup.class);
            boolean validateCaptcha = loginRequest.getCaptcha().equalsIgnoreCase(loginProps.getMagicCaptcha()) || captchaService.validate(loginRequest.getUuid(), loginRequest.getCaptcha());
            AssertUtils.isFalse(validateCaptcha, ErrorCode.CAPTCHA_ERROR);
        }

        // 登录用户
        UserEntity user;
        if (UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name().equalsIgnoreCase(loginRequest.getType()) || UcConst.LoginTypeEnum.APP_USER_PWD.name().equalsIgnoreCase(loginRequest.getType())) {
            // 帐号密码登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.UsernamePasswordGroup.class);
            user = userService.getOneByColumn("username", loginRequest.getUsername());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
            AssertUtils.isFalse(DigestUtil.bcryptCheck(loginRequest.getPassword(), user.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);
        } else if (UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name().equalsIgnoreCase(loginRequest.getType()) || UcConst.LoginTypeEnum.APP_MOBILE_SMS.name().equalsIgnoreCase(loginRequest.getType())) {
            // 手机号验证码登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.MobileSmsCodeGroup.class);
            user = userService.getOneByColumn("mobile", loginRequest.getMobile());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);

            // 验证码登录的,先校验是否和用户的安全码相同
            if (loginRequest.getSmsCode().equalsIgnoreCase(user.getVerifyCode())) {
                // 安全码验证通过
            } else {
                //  校验验证码
                MailLogEntity lastSmsLog = mailLogService.findLastLogByTplCode(MsgConst.SMS_TPL_LOGIN, loginRequest.getMobile());
                AssertUtils.isNull(lastSmsLog, ErrorCode.SMS_CODE_ERROR);
                AssertUtils.isFalse(loginRequest.getSmsCode().equalsIgnoreCase(JacksonUtils.jsonToMap(lastSmsLog.getContentParams()).get("code").toString()), ErrorCode.SMS_CODE_ERROR);
                // 验证码正确,校验过期时间
                AssertUtils.isTrue(lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date()), ErrorCode.SMS_CODE_EXPIRED);
                // 将短信消费掉
                mailLogService.consumeById(lastSmsLog.getId());
            }
        } else if (UcConst.LoginTypeEnum.APP_APPLE.name().equalsIgnoreCase(loginRequest.getType())) {
            // 苹果登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.AppleGroup.class);
            // jwt解析identityToken, 获取userIdentifier
            JWT jwt = JWT.of(loginRequest.getAppleIdentityToken());
            // app包名
            String packageName = jwt.getHeader("audience").toString();
            // 用户id
            String userIdentifier = jwt.getPayload("subject").toString();
            // 有效期
            AssertUtils.isTrue(new Date((long) jwt.getPayload("exp")).after(new Date()), ErrorCode.APPLE_LOGIN_ERROR);

            // todo 使用apple keys做验证
            // {https://developer.apple.com/cn/app-store/review/guidelines/#sign-in-with-apple}
            // 通过packageName和userIdentifier找对应的数据记录
            UserOauthEntity userApple = userOauthService.getByOpenid(userIdentifier);
            if (userApple == null) {
                // 不存在记录,则保存记录
                userApple = new UserOauthEntity();
                userApple.setAppid(packageName);
                userApple.setOpenid(userIdentifier);
                userApple.setType(UcConst.OauthTypeEnum.APPLE.name());
                userOauthService.save(userApple);
            }
            AssertUtils.isNull(userApple.getUserId(), ErrorCode.APPLE_NOT_BIND);

            user = userService.getById(userApple.getUserId());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_DISABLE);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_NOT_EXIST);
        } else {
            throw new OnexException(ErrorCode.UNKNOWN_LOGIN_TYPE);
        }

        /*if (user == null && loginChannelCfg.isAutoCreate()) {
            // 没有该用户，并且需要自动创建用户
            user = new UserDTO();
            user.setState(UcConst.UserStateEnum.ENABLED.value());
            user.setMobile(loginRequest.getMobile());
            user.setUsername(loginRequest.getMobile());
            user.setType(UcConst.UserTypeEnum.USER.value());
            user.setGender(3);
            // 密码加密
            user.setPassword(PasswordUtils.encode(loginRequest.getMobile()));
            saveDto(user);
            //保存角色用户关系
            roleUserService.saveOrUpdate(user.getId(), user.getRoleIdList());
        }*/

        // 登录成功
        return user;
    }

}

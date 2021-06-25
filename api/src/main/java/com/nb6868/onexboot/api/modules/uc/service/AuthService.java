package com.nb6868.onexboot.api.modules.uc.service;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.common.config.LoginProps;
import com.nb6868.onexboot.api.common.config.LoginPropsSource;
import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dingtalk.DingtalkScanProps;
import com.nb6868.onexboot.api.modules.uc.dto.LoginRequest;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.api.modules.uc.wx.WxScanProps;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.ValidatorUtils;
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
    private OnexProps onexProps;
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
    private ParamService paramService;
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
        permissionsList.forEach(permissions -> {
            set.addAll(StrSplitter.splitTrim(permissions, ',', true));
        });
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

    /**
     * 通过登录类型获取登录配置
     *
     * @param type 类型
     * @return 登录配置
     */
    public LoginProps getLoginProps(String type) {
        if (UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name().equalsIgnoreCase(type)
                && null != onexProps.getLoginAdminProps().getUsernamePasswordLoginProps()
                && onexProps.getLoginAdminProps().getUsernamePasswordLoginProps().getSource() == LoginPropsSource.PROPS) {
            return onexProps.getLoginAdminProps().getUsernamePasswordLoginProps();
        } else if (UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name().equalsIgnoreCase(type)
                && null != onexProps.getLoginAdminProps().getMobileSmscodeLoginProps()
                && onexProps.getLoginAdminProps().getMobileSmscodeLoginProps().getSource() == LoginPropsSource.PROPS) {
            return onexProps.getLoginAdminProps().getMobileSmscodeLoginProps();
        } else if (UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name().equalsIgnoreCase(type)
                && null != onexProps.getLoginAdminProps().getDingtalkScanLoginProps()
                && onexProps.getLoginAdminProps().getDingtalkScanLoginProps().getSource() == LoginPropsSource.PROPS) {
            return onexProps.getLoginAdminProps().getDingtalkScanLoginProps();
        } else if (UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name().equalsIgnoreCase(type)
                && null != onexProps.getLoginAdminProps().getWechatScanLoginProps()
                && onexProps.getLoginAdminProps().getWechatScanLoginProps().getSource() == LoginPropsSource.PROPS) {
            return onexProps.getLoginAdminProps().getWechatScanLoginProps();
        }
        return paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + type, LoginProps.class);
    }

    public OnexProps.LoginAppProps getLoginAppProps() {
        OnexProps.LoginAppProps loginAppProps = onexProps.getLoginAppProps();
        if (loginAppProps.getSource() == LoginPropsSource.DB) {
            loginAppProps = paramService.getContentObject(UcConst.LOGIN_APP, OnexProps.LoginAppProps.class);
        }
        return loginAppProps;
    }

    /**
     * 获得后台登录配置
     */
    public OnexProps.LoginAdminProps getLoginAdminProps() {
        OnexProps.LoginAdminProps loginAdminProps = onexProps.getLoginAdminProps();
        if (loginAdminProps.getSource() == LoginPropsSource.DB) {
            loginAdminProps = paramService.getContentObject(UcConst.LOGIN_ADMIN, OnexProps.LoginAdminProps.class);
        }
        return loginAdminProps;
    }

    /**
     * 获得详细的后台登录配置
     */
    public OnexProps.LoginAdminProps getLoginAdminDetailProps() {
        OnexProps.LoginAdminProps loginAdminProps = getLoginAdminProps();
        // 各个登录途径的登录配置,需要确认是否从db读取
        if (loginAdminProps.isUsernamePasswordLogin() && loginAdminProps.getUsernamePasswordLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setUsernamePasswordLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_USERNAME_PASSWORD.name(), LoginProps.class));
        }
        if (loginAdminProps.isMobileSmscodeLogin() && loginAdminProps.getMobileSmscodeLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setMobileSmscodeLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name(), LoginProps.class));
        }
        if (loginAdminProps.isWechatScanLogin() && loginAdminProps.getWechatScanLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setWechatScanLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name(), LoginProps.class));
            loginAdminProps.setWechatScanProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_WECHAT_SCAN.name() + "_CONFIG", WxScanProps.class));
        }
        if (loginAdminProps.isDingtalkScanLogin() && loginAdminProps.getDingtalkScanLoginProps().getSource() == LoginPropsSource.DB) {
            loginAdminProps.setDingtalkScanLoginProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name(), LoginProps.class));
            loginAdminProps.setDingtalkScanProps(paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + UcConst.LoginTypeEnum.ADMIN_DINGTALK_SCAN.name() + "_CONFIG", DingtalkScanProps.class));
        }
        return loginAdminProps;
    }

    /**
     * 登录获取用户
     */
    public UserEntity login(LoginRequest loginRequest, LoginProps loginProps) {
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
            if (user == null) {
                // 帐号不存在
                throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                // 帐号锁定
                throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
            } else if (!DigestUtil.bcryptCheck(loginRequest.getPassword(), user.getPassword())) {
                // 密码不匹配
                throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
            }
        } else if (UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name().equalsIgnoreCase(loginRequest.getType()) || UcConst.LoginTypeEnum.APP_MOBILE_SMS.name().equalsIgnoreCase(loginRequest.getType())) {
            // 手机号验证码登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.MobileSmsCodeGroup.class);
            user = userService.getOneByColumn("mobile", loginRequest.getMobile());
            if (user == null) {
                // 帐号不存在
                throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
            } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                // 帐号锁定
                throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
            }
            // 验证码登录的,先校验是否和用户的安全码相同
            if (loginRequest.getSmsCode().equalsIgnoreCase(user.getVerifyCode())) {
                // 安全码验证通过
            } else {
                //  校验验证码
                MailLogEntity lastSmsLog = mailLogService.findLastLogByTplCode(MsgConst.SMS_TPL_LOGIN, loginRequest.getMobile());
                if (null == lastSmsLog || !loginRequest.getSmsCode().equalsIgnoreCase(JacksonUtils.jsonToMap(lastSmsLog.getContentParams()).get("code").toString())) {
                    // 验证码错误,找不到验证码
                    throw new OnexException(ErrorCode.SMS_CODE_ERROR);
                } else {
                    // 验证码正确
                    // 校验过期时间
                    if (lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date())) {
                        throw new OnexException(ErrorCode.SMS_CODE_EXPIRED);
                    }
                    // 将短信消费掉
                    mailLogService.consumeById(lastSmsLog.getId());
                }
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
            Date expireTime = new Date((long) jwt.getPayload("exp"));
            if (expireTime.after(new Date())) {
                throw new OnexException(ErrorCode.APPLE_LOGIN_ERROR);
            } else {
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
                if (userApple.getUserId() == null) {
                    // 未绑定用户
                    throw new OnexException(ErrorCode.APPLE_NOT_BIND);
                } else {
                    user = userService.getById(userApple.getUserId());
                    if (user == null) {
                        // 帐号不存在
                        throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
                    } else if (user.getState() != UcConst.UserStateEnum.ENABLED.value()) {
                        // 帐号锁定
                        throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
                    }
                }
            }
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

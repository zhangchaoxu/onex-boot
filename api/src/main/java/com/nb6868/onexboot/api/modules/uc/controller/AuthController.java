package com.nb6868.onexboot.api.modules.uc.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nb6868.onexboot.api.common.annotation.LogLogin;
import com.nb6868.onexboot.api.common.config.LoginProps;
import com.nb6868.onexboot.api.common.config.OnexProps;
import com.nb6868.onexboot.api.common.util.AESUtils;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.entity.MailLogEntity;
import com.nb6868.onexboot.api.modules.msg.service.MailLogService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dto.LoginRequest;
import com.nb6868.onexboot.api.modules.uc.dto.RegisterRequest;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.PasswordUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.ValidatorUtils;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 认证授权相关接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("uc/auth")
@Validated
@Api(tags = "用户认证")
public class AuthController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private MailLogService mailLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserOauthService userOauthService;
    @Autowired
    private ShiroService shiroService;

    @GetMapping("getLoginAdminProps")
    @ApiOperation("获得后台登录配置")
    public Result<?> getLoginAdminProps() {
        OnexProps.LoginAdminProps loginAdminProps = shiroService.getLoginAdminDetailProps();
        return new Result<>().success(loginAdminProps);
    }

    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation(value = "登录")
    @LogLogin
    public Kv login(LoginRequest loginRequest) {
        // 获得登录配置
        LoginProps loginProps = shiroService.getLoginProps(UcConst.LOGIN_TYPE_PREFIX + loginRequest.getType());
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

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
            } else if (!PasswordUtils.matches(loginRequest.getPassword(), user.getPassword())) {
                // 密码不匹配
                throw new OnexException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
            }
        }  else if (UcConst.LoginTypeEnum.ADMIN_MOBILE_SMSCODE.name().equalsIgnoreCase(loginRequest.getType()) || UcConst.LoginTypeEnum.APP_MOBILE_SMS.name().equalsIgnoreCase(loginRequest.getType())) {
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
        } else if (UcConst.LoginTypeEnum.APP_APPLE.name().equalsIgnoreCase(loginRequest.getType())) {
            // 苹果登录
            ValidatorUtils.validateEntity(loginRequest, LoginRequest.AppleGroup.class);
            // jwt解析identityToken, 获取userIdentifier
            DecodedJWT jwt = JWT.decode(loginRequest.getAppleIdentityToken());
            // app包名
            String packageName = jwt.getAudience().get(0);
            // 用户id
            String userIdentifier = jwt.getSubject();
            // 有效期
            Date expireTime = jwt.getExpiresAt();
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
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginProps));
        kv.set("expire", loginProps.getTokenExpire());
        kv.set("user", user);
        return kv;
    }

    /**
     * 加密登录
     * 逻辑同login接口
     */
    @SneakyThrows
    @PostMapping("loginEncrypt")
    @ApiOperation(value = "加密登录")
    @LogLogin
    public Result<?> loginEncrypt(@RequestBody String loginEncrypted) {
        // 密文转json明文
        String loginRaw = AESUtils.decrypt(URLDecoder.decode(loginEncrypted, StandardCharsets.UTF_8.name()));
        // json明文转实体
        LoginRequest loginRequest = JacksonUtils.jsonToPojo(loginRaw, LoginRequest.class);
        // 效验数据
        ValidatorUtils.validateEntity(loginRequest, DefaultGroup.class);
        return new Result<>().success(login(loginRequest));
    }

    /**
     * 注册
     */
    @PostMapping("register")
    @ApiOperation(value = "注册")
    public Result<?> register(@Validated @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

}

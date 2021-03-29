package com.nb6868.onexboot.api.modules.uc.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
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
import com.nb6868.onexboot.api.modules.uc.dingtalk.DingTalkApi;
import com.nb6868.onexboot.api.modules.uc.dingtalk.GetUserInfoByCodeResponse;
import com.nb6868.onexboot.api.modules.uc.dto.*;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.api.modules.uc.wx.WxApiService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.PasswordUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.ValidatorUtils;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
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
    private AuthService shiroService;
    @Autowired
    private WxApiService wxApiService;

    @GetMapping("getLoginAdminProps")
    @ApiOperation("获得后台登录配置")
    public Result<?> getLoginAdminProps() {
        OnexProps.LoginAdminProps loginAdminProps = shiroService.getLoginAdminDetailProps();
        return new Result<>().success(loginAdminProps);
    }

    @GetMapping("getLoginAppProps")
    @ApiOperation("获得前台登录配置")
    public Result<?> getLoginAppProps() {
        OnexProps.LoginAppProps loginAppProps = shiroService.getLoginAppProps();
        return new Result<>().success(loginAppProps);
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

    /**
     * 微信小程序Oauth授权登录
     */
    @PostMapping("/wxMaLoginByCodeAndUserInfo")
    @ApiOperation("Oauth授权登录")
    @LogLogin
    public Result<?> wxMaLoginByCodeAndUserInfo(@Validated @RequestBody OauthWxMaLoginByCodeAndUserInfoRequest request) throws WxErrorException {
        LoginProps loginProps = shiroService.getLoginProps(request.getParamCode());
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录
        WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(jscode2SessionResult.getSessionKey(), request.getRawData(), request.getSignature())) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());

        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaUserInfo(wxService.getWxMaConfig().getAppid(), userInfo);
        // 用户
        UserEntity user = null;
        if (userOauth.getUserId() != null) {
            user = userService.getById(userOauth.getUserId());
            if (null == user) {
                // 如果用户空了,同时结束所有绑定关系
                userOauthService.unbindByUserId(userOauth.getUserId());
            }
        }
        if (user == null) {
            // 根据业务提示错误或者自动创建用户
            return new Result<>().error(ErrorCode.OAUTH_NOT_BIND_ERROR);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginProps));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

    /**
     * Oauth授权登录
     */
    @PostMapping("/wxMaLoginByCode")
    @ApiOperation("Oauth微信小程序授权登录")
    @LogLogin
    public Result<?> wxMaLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) throws WxErrorException {
        LoginProps loginProps = shiroService.getLoginProps(request.getParamCode());
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaJscode2SessionResult(wxService.getWxMaConfig().getAppid(), jscode2SessionResult);
        // 用户
        UserEntity user = null;
        if (userOauth.getUserId() != null) {
            user = userService.getById(userOauth.getUserId());
            if (null == user) {
                // 如果用户空了,同时结束所有绑定关系
                userOauthService.unbindByUserId(userOauth.getUserId());
            }
        }
        if (user == null) {
            // 根据业务提示错误或者自动创建用户
            return new Result<>().error(ErrorCode.OAUTH_NOT_BIND_ERROR);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginProps));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

    /**
     * Oauth授权登录
     */
    @PostMapping("/wxMaLoginByPhone")
    @ApiOperation("Oauth微信小程序手机号授权登录")
    @LogLogin
    public Result<?> wxMaLoginByPhone(@Validated @RequestBody OauthWxMaLoginByCodeAndPhone request) throws WxErrorException {
        LoginProps loginProps = shiroService.getLoginProps(request.getParamCode());
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = wxApiService.getWxMaService(request.getParamCode());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        // 解密用户手机号
        WxMaPhoneNumberInfo phoneNumberInfo = wxService.getUserService().getPhoneNoInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());
        UserEntity user = userService.getByMobile(phoneNumberInfo.getCountryCode(), phoneNumberInfo.getPurePhoneNumber());
        if (user == null) {
            // todo 用户不存在,按照实际业务需求创建用户或者提示用户不存在
            user = new UserEntity();
            user.setMobileArea(phoneNumberInfo.getCountryCode());
            user.setMobile(phoneNumberInfo.getPurePhoneNumber());
            user.setUsername(phoneNumberInfo.getPurePhoneNumber());
            user.setPassword(PasswordUtils.encode(phoneNumberInfo.getPurePhoneNumber()));
            user.setState(UcConst.UserStateEnum.ENABLED.value());
            user.setType(UcConst.UserTypeEnum.USER.value());
            userService.save(user);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginProps));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

    /**
     * 钉钉扫码授权登录，通过code登录
     * see https://ding-doc.dingtalk.com/document/app/scan-qr-code-to-log-on-to-third-party-websites
     */
    @PostMapping("/dingtalkLoginByCode")
    @ApiOperation("钉钉扫码授权登录")
    @LogLogin
    public Result<?> dingtalkLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) {
        LoginProps loginProps = shiroService.getLoginProps(request.getParamCode());
        AssertUtils.isNull(loginProps, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 1. 根据sns临时授权码获取用户信息
        GetUserInfoByCodeResponse userInfoByCodeResponse = DingTalkApi.getUserInfoByCode("", "", request.getCode());
        if (userInfoByCodeResponse.isSuccess()) {
            // todo 钉钉接口处理流程
            return new Result<>().success(userInfoByCodeResponse.getUser_info());
        } else {
            return new Result<>().error(userInfoByCodeResponse.getErrcode() + ":" + userInfoByCodeResponse.getErrmsg());
        }
    }

}

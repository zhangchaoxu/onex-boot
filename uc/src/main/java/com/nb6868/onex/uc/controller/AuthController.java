package com.nb6868.onex.uc.controller;

import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.*;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.dingtalk.GetUserInfoByCodeResponse;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.EncryptForm;
import com.nb6868.onex.common.pojo.LoginResult;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.util.TreeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.wechat.WechatMaPropsConfig;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.dto.MenuTreeDTO;
import com.nb6868.onex.uc.dto.RegisterRequest;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.entity.UserOauthEntity;
import com.nb6868.onex.uc.service.AuthService;
import com.nb6868.onex.uc.service.MenuService;
import com.nb6868.onex.uc.service.TokenService;
import com.nb6868.onex.uc.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sun.net.util.URLUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证授权相关接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/uc/auth/")
@Validated
@Api(tags = "用户认证")
public class AuthController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailLogService mailLogService;
    @Autowired
    private MenuService menuService;

    @GetMapping("getLoginSettings")
    @ApiOperation("获得登录设置")
    public Result<?> getLoginSettings(@RequestParam String type) {
        AuthProps.Settings loginSettings = authService.getLoginSettings(type);
        AssertUtils.isNull(loginSettings, "未定义该类型");

        return new Result<>().success(loginSettings);
    }

    @GetMapping("getLoginConfig")
    @ApiOperation("获得登录配置")
    public Result<?> getLoginConfig(@RequestParam String type) {
        AuthProps.Config loginConfig = authService.getLoginConfig(type);
        AssertUtils.isNull(loginConfig, "未定义该类型");

        return new Result<>().success(loginConfig);
    }

    @PostMapping("sendLoginCode")
    @ApiOperation("发送登录验证码消息")
    @LogOperation("发送登录验证码消息")
    public Result<?> sendLoginCode(@Validated(value = {AddGroup.class}) @RequestBody MailSendForm dto) {
        // 只允许发送CODE_开头的模板
        AssertUtils.isFalse(dto.getTplCode().startsWith(MsgConst.SMS_CODE_TPL_PREFIX), "只支持" + MsgConst.SMS_CODE_TPL_PREFIX + "类型模板发送");
        boolean flag = mailLogService.send(dto);
        if (flag) {
            return new Result<>();
        }
        return new Result<>().error("消息发送失败");
    }

    @PostMapping("login")
    @AccessControl
    @ApiOperation(value = "登录")
    @LogOperation(value = "登录", type = "login")
    public Result<?> login(@Validated(value = {DefaultGroup.class}) @RequestBody LoginForm loginRequest) {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(loginRequest.getAuthConfigType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        UserEntity user = authService.login(loginRequest, loginConfig);

        // 登录成功
        LoginResult loginResult = new LoginResult()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(tokenService.createToken(user, loginConfig))
                .setTokenKey(UcConst.TOKEN_HEADER);
        return new Result<>().success(loginResult);
    }

    @SneakyThrows
    @PostMapping("loginEncrypt")
    @AccessControl
    @ApiOperation(value = "加密登录")
    @LogOperation(value = "加密登录", type = "login")
    public Result<?> loginEncrypt(@RequestBody EncryptForm form) {
        // 密文->urldecode->aes解码->原明文->json转实体
        String json = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(URLUtil.decode(form.getBody()));
        LoginForm loginRequest = JacksonUtils.jsonToPojo(json, LoginForm.class);
        // 效验数据
        ValidatorUtils.validateEntity(loginRequest, DefaultGroup.class);
        return login(loginRequest);
    }

    @PostMapping("register")
    @AccessControl
    @ApiOperation(value = "注册")
    public Result<?> register(@Validated @RequestBody RegisterRequest request) {
        UserEntity userEntity = userService.register(request);
        UserDTO userDTO = ConvertUtils.sourceToTarget(userEntity, UserDTO.class);
        return new Result<>().success(userDTO);
    }

    @Deprecated
    @PostMapping("/wxMaLoginByCodeAndUserInfo")
    @ApiOperation("Oauth授权登录")
    @LogOperation(value = "Oauth授权登录", type = "login")
    public Result<?> wxMaLoginByCodeAndUserInfo(@Validated @RequestBody OauthWxMaLoginByCodeAndUserInfoForm request) throws WxErrorException {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getAuthConfigType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录
        WxMaService wxService = WechatMaPropsConfig.getService(request.getAuthConfigType());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(jscode2SessionResult.getSessionKey(), request.getRawData(), request.getSignature())) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());

        // 更新或者插入Oauth表
        UserOauthEntity userOauth = userOauthService.saveOrUpdateByWxMaUserInfo(wxService.getWxMaConfig().getAppid(), userInfo, jscode2SessionResult.getOpenid(), jscode2SessionResult.getUnionid());
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
        Dict dict = Dict.create();
        dict.set("tokenKey", UcConst.TOKEN_HEADER);
        dict.set("token", tokenService.createToken(user, loginConfig));
        dict.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    @PostMapping("/wxMaLoginByCode")
    @ApiOperation("Oauth微信小程序授权登录")
    @LogOperation(value = "Oauth微信小程序授权登录", type = "login")
    public Result<?> wxMaLoginByCode(@Validated @RequestBody OauthLoginByCodeForm request) throws WxErrorException {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = WechatMaPropsConfig.getService(request.getType());
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
        Dict dict = Dict.create()
                .set("tokenKey", UcConst.TOKEN_HEADER)
                .set("token", tokenService.createToken(user, loginConfig))
                .set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    @PostMapping("/wxMaLoginByPhone")
    @ApiOperation("Oauth微信小程序手机号授权登录")
    @LogOperation(value = "Oauth微信小程序手机号授权登录", type = "login")
    public Result<?> wxMaLoginByPhone(@Validated @RequestBody OauthWxMaLoginByCodeAndPhoneForm request) throws WxErrorException {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getAuthConfigType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 微信登录(小程序)
        WxMaService wxService = WechatMaPropsConfig.getService(request.getWechatMaConfigType());
        WxMaJscode2SessionResult jscode2SessionResult = wxService.getUserService().getSessionInfo(request.getCode());
        // 解密用户手机号
        WxMaPhoneNumberInfo phoneNumberInfo = wxService.getUserService().getPhoneNoInfo(jscode2SessionResult.getSessionKey(), request.getEncryptedData(), request.getIv());
        UserEntity user = userService.getOneByColumn("mobile", phoneNumberInfo.getPurePhoneNumber());
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
        Dict dict = Dict.create()
                .set("tokenKey", UcConst.TOKEN_HEADER)
                .set("token", tokenService.createToken(user, loginConfig))
                .set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(dict);
    }

    /**
     * 钉钉扫码授权登录，通过code登录
     * see https://ding-doc.dingtalk.com/document/app/scan-qr-code-to-log-on-to-third-party-websites
     */
    @PostMapping("/dingtalkLoginByCode")
    @ApiOperation("钉钉扫码授权登录")
    @LogOperation(value = "钉钉扫码授权登录", type = "login")
    public Result<?> dingtalkLoginByCode(@Validated @RequestBody OauthLoginByCodeForm request) {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(request.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 1. 根据sns临时授权码获取用户信息
        GetUserInfoByCodeResponse userInfoByCodeResponse = DingTalkApi.getUserInfoByCode("", "", request.getCode());
        if (userInfoByCodeResponse.isSuccess()) {
            // todo 钉钉接口处理流程
            return new Result<>().success(userInfoByCodeResponse.getUser_info());
        } else {
            return new Result<>().error(userInfoByCodeResponse.getErrcode() + ":" + userInfoByCodeResponse.getErrmsg());
        }
    }

    @GetMapping("menuScope")
    @ApiOperation("权限范围")
    public Result<?> scope() {
        ShiroUser user = ShiroUtils.getUser();
        // 获取该用户所有menu
        List<MenuEntity> allList = menuService.getListByUser(user, null);
        // 过滤出其中显示菜单
        List<MenuTreeDTO> menuList = new ArrayList<>();
        // 过滤出其中路由菜单
        List<MenuDTO> urlList = new ArrayList<>();
        // 过滤出其中的权限
        Set<String> permissions = new HashSet<>();
        allList.forEach(menu -> {
            if (menu.getShowMenu() == 1 && menu.getType() == UcConst.MenuTypeEnum.MENU.value()) {
                menuList.add(ConvertUtils.sourceToTarget(menu, MenuTreeDTO.class));
            }
            if (StrUtil.isNotBlank(menu.getUrl())) {
                urlList.add(ConvertUtils.sourceToTarget(menu, MenuDTO.class));
            }
            if (StrUtil.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StrSplitter.splitTrim(menu.getPermissions(), ',', true));
            }
        });
        // 将菜单列表转成菜单树
        List<MenuTreeDTO> menuTree = TreeUtils.build(menuList);
        // 获取角色列表
        Set<String> roles = authService.getUserRoles(user);
        return new Result<>().success(Dict.create()
                .set("menuTree", menuTree)
                .set("urlList", urlList)
                .set("permissions", permissions)
                .set("roles", roles));
    }

}

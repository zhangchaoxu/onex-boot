package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.auth.ChangePasswordForm;
import com.nb6868.onex.common.auth.LoginForm;
import com.nb6868.onex.common.auth.LoginResult;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.EncryptForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.*;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.entity.MailTplEntity;
import com.nb6868.onex.msg.service.MailLogService;
import com.nb6868.onex.msg.service.MailTplService;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.*;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
import com.pig4cloud.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/uc/auth/")
@Validated
@Api(tags = "用户授权", position = 10)
public class AuthController {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ParamsService paramsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailTplService mailTplService;
    @Autowired
    private MailLogService mailLogService;

    @PostMapping("loginParams")
    @AccessControl
    @ApiOperation(value = "登录参数", notes = "Anon")
    @ApiOperationSupport(order = 10)
    public Result<?> loginParams(@Validated @RequestBody TenantParamsInfoByUrlForm form) {
        // 通过url地址获得租户配置
        JSONObject paramsContent = paramsService.getContent(UcConst.ParamsTypeEnum.TENANT.value(), null, null, UcConst.PARAMS_CODE_LOGIN, "url", form.getUrl());
        return new Result<>().success(paramsContent);
    }

    @PostMapping("captcha")
    @AccessControl
    @ApiOperation(value = "图形验证码(base64)", notes = "Anon@验证时需将uuid和验证码内容一起提交")
    @ApiOperationSupport(order = 20)
    public Result<?> captcha(@Validated @RequestBody CaptchaForm form) {
        String uuid = IdUtil.fastSimpleUUID();
        // 随机arithmetic/spec
        Captcha captcha = captchaService.createCaptcha(uuid, form.getWidth(), form.getHeight(), RandomUtil.randomEle(new String[]{"spec"}));
        // 将uuid和图片base64返回给前端
        return new Result<>().success(Dict.create().set("uuid", uuid).set("image", captcha.toBase64()));
    }

    @PostMapping("sendMailCode")
    @AccessControl
    @ApiOperation("发送验证码消息")
    @LogOperation("发送验证码消息")
    @ApiOperationSupport(order = 30)
    public Result<?> sendMailCode(@Validated(value = {DefaultGroup.class}) @RequestBody MailSendForm form) {
        MailTplEntity mailTpl = mailTplService.getByCode(form.getTenantCode(), form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "模板不存在");
        if (mailTpl.getParams().getBool("verifyUserExist", false)) {
            // 是否先验证用户是否存在
            UserEntity user = userService.getByMobile(form.getTenantCode(), form.getMailTo());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
        }
        boolean flag = mailLogService.send(mailTpl, form);
        return new Result<>().boolResult(flag);
    }

    @PostMapping("userLogin")
    @AccessControl
    @ApiOperation(value = "用户登录", notes = "Anon")
    @LogOperation(value = "用户登录", type = "login")
    @ApiOperationSupport(order = 100)
    public Result<?> userLogin(@Validated(value = {DefaultGroup.class}) @RequestBody LoginForm form) {
        // 获得登录配置
        AuthProps.Config loginConfig = authProps.getConfigs().get(form.getType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);
        // 获得登录参数
        JSONObject loginParams = paramsService.getContent(UcConst.ParamsTypeEnum.TENANT.value(), form.getTenantCode(), null, UcConst.PARAMS_CODE_LOGIN, "type", form.getType());
        // 验证验证码
        if (loginParams.getBool("captcha", false)) {
            ValidatorUtils.validateEntity(form, LoginForm.CaptchaGroup.class);
            AssertUtils.isFalse(captchaService.validate(form.getCaptchaUuid(), form.getCaptchaValue()), ErrorCode.CAPTCHA_ERROR);
        }
        UserEntity user;
        if (form.getType().endsWith("USERNAME_PASSWORD")) {
            // 帐号密码登录
            user = authService.loginByUsernameAndPassword(form, loginParams);
        } else if (form.getType().endsWith("MOBILE_SMS")) {
            // 手机号验证码登录
            user = authService.loginByMobileAndSms(form, loginParams);
        } else {
            // 其它登录方式
            throw new OnexException(ErrorCode.UNKNOWN_LOGIN_TYPE);
        }

        // 登录成功
        LoginResult loginResult = new LoginResult()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(tokenService.createToken(user, loginConfig))
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<>().success(loginResult);
    }

    @PostMapping("userLoginEncrypt")
    @AccessControl
    @ApiOperation(value = "用户登录(加密)", notes = "Anon@将userLogin接口数据,做AES加密作为body的值")
    @LogOperation(value = "用户登录(加密)", type = "login")
    @ApiOperationSupport(order = 110)
    public Result<?> userLoginEncrypt(@Validated @RequestBody EncryptForm encryptForm) {
        // 密文->aes解码->原明文->json转实体
        String json = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(encryptForm.getBody());
        LoginForm form = JacksonUtils.jsonToPojo(json, LoginForm.class);
        return ((AuthController) AopContext.currentProxy()).userLogin(form);
    }

    @PostMapping("userLogout")
    @ApiOperation(value = "用户登出")
    @LogOperation(value = "用户登出", type = "logout")
    @ApiOperationSupport(order = 120)
    public Result<?> userLogout() {
        String token = HttpContextUtils.getRequestParameter(authProps.getTokenHeaderKey());
        tokenService.deleteToken(token);
        return new Result<>();
    }

    @PostMapping("userInfo")
    @ApiOperation("用户信息")
    @ApiOperationSupport(order = 200)
    public Result<?> userInfo() {
        UserEntity user = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);

        UserDTO data = ConvertUtils.sourceToTarget(user, UserDTO.class);
        return new Result<>().success(data);
    }

    @PostMapping("userMenuScope")
    @ApiOperation("用户权限范围")
    @ApiOperationSupport(order = 200)
    public Result<MenuScopeResult> userMenuScope(@Validated @RequestBody MenuScopeForm form) {
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
            if (form.isPermissions() && StrUtil.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StrSplitter.splitTrim(menu.getPermissions(), ',', true));
            }
        });
        // 将菜单列表转成菜单树
        MenuScopeResult result = new MenuScopeResult();
        result.setMenuTree(TreeUtils.build(menuList));
        result.setUrlList(urlList);
        if (form.isPermissions()) {
            result.setPermissions(permissions);
        }
        if (form.isRoles()) {
            // 获取角色列表
            Set<String> roles = userService.getUserRoles(user);
            result.setRoles(roles);
        }
        return new Result<MenuScopeResult>().success(result);
    }

    @PostMapping("userChangePassword")
    @ApiOperation("用户修改密码")
    @LogOperation("用户修改密码")
    public Result<?> userChangePassword(@Validated @RequestBody ChangePasswordForm form) {
        String tenantCode = ShiroUtils.getUserTenantCode();
        // 先校验密码复杂度
        JSONObject paramsContent = paramsService.getContent(null, tenantCode, null, UcConst.PARAMS_CODE_LOGIN);
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(paramsContent.getStr("passwordRegExp")) && !ReUtil.isMatch(paramsContent.getStr("passwordRegExp"), form.getNewPassword()), ErrorCode.ERROR_REQUEST, paramsContent.getStr("passwordRegError", "密码不符合规则"));
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验原密码
        AssertUtils.isFalse(PasswordUtils.verify(form.getPassword(), data.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 更新密码
        userService.updatePassword(data.getId(), form.getNewPassword());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIds(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("userChangePasswordEncrypt")
    @ApiOperation("用户修改密码(加密)")
    @LogOperation("用户修改密码(加密)")
    public Result<?> userChangePasswordEncrypt(@Validated @RequestBody EncryptForm encryptForm) {
        // 密文->aes解码->原明文->json转实体
        String json = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(encryptForm.getBody());
        ChangePasswordForm form = JacksonUtils.jsonToPojo(json, ChangePasswordForm.class);
        return ((AuthController) AopContext.currentProxy()).userChangePassword(form);
    }

}

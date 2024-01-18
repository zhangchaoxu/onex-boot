package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.*;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgLogBody;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.msg.MsgTplBody;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.EncryptForm;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.*;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.*;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "用户授权")
@Slf4j
public class AuthController {

    @Autowired
    AuthProps authProps;
    @Autowired
    UserService userService;
    @Autowired
    CaptchaService captchaService;
    @Autowired
    TokenService tokenService;
    @Autowired
    MenuService menuService;
    @Autowired
    ParamsService paramsService;
    @Autowired
    AuthService authService;
    @Autowired
    BaseMsgService msgService;

    @PostMapping("captcha")
    @AccessControl
    @Operation(summary = "图形验证码(base64)", description = "Anon")
    @ApiOperationSupport(order = 10)
    public Result<?> captcha(@Validated @RequestBody CaptchaForm form) {
        String uuid = IdUtil.fastSimpleUUID();
        String captchaBase64 = captchaService.createCaptchaBase64(uuid, form.getWidth(), form.getHeight());
        // 将uuid和图片base64返回给前端
        Dict result = Dict.create().set("uuid", uuid).set("image", captchaBase64);
        return new Result<>().success(result);
    }

    @PostMapping("sendMsgCode")
    @AccessControl
    @Operation(summary = "发送验证码消息", description = "Anon")
    @LogOperation("发送验证码消息")
    @ApiOperationSupport(order = 20)
    public Result<?> sendMsgCode(@Validated(value = {DefaultGroup.class}) @RequestBody MsgSendForm form) {
        MsgTplBody mailTpl = msgService.getTplByCode(form.getTenantCode(), form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");
        if (mailTpl.getParams().getBool("verifyUserExist", false)) {
            // 是否先验证用户是否存在
            UserEntity user = userService.getByMobile(form.getTenantCode(), form.getMailTo());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
        }
        // 结果标记
        boolean flag = msgService.sendMail(form);
        if (flag) {
            return new Result<>().success("短信发送成功", null);
        } else {
            return new Result<>().error("短信发送失败");
        }
    }

    @PostMapping("userLogin")
    @AccessControl
    @Operation(summary = "用户登录", description = "Anon")
    @LogOperation(value = "用户登录", type = "login")
    @ApiOperationSupport(order = 100)
    public Result<?> userLogin(@Validated(value = {DefaultGroup.class}) @RequestBody LoginForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]对应的登录配置");
        // 验证验证码
        if (loginParams.getBool("captcha", false)) {
            // 先检验验证码表单
            ValidatorUtils.validateEntity(form, LoginForm.CaptchaGroup.class);
            // 再校验验证码与魔术验证码不同，并且 校验失败
            AssertUtils.isTrue(!form.getCaptchaValue().equalsIgnoreCase(loginParams.getStr("magicCaptcha")) && !captchaService.validate(form.getCaptchaUuid(), form.getCaptchaValue()), ErrorCode.CAPTCHA_ERROR);
        }
        UserEntity user;
        if (form.getType().endsWith("USERNAME_PASSWORD")) {
            // 帐号密码登录
            user = authService.loginByUsernamePassword(form, loginParams);
        } else if (form.getType().endsWith("MOBILE_SMS")) {
            // 手机号验证码登录
            user = authService.loginByMobileSms(form, loginParams);
        } else {
            // 其它登录方式
            throw new OnexException(ErrorCode.UNKNOWN_LOGIN_TYPE);
        }

        // 创建token
        String token = tokenService.createToken(user,
                loginParams.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE),
                form.getType(),
                loginParams.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE),
                loginParams.getInt(AuthConst.TOKEN_EXPIRE_KEY, AuthConst.TOKEN_EXPIRE_VALUE),
                loginParams.getInt(AuthConst.TOKEN_LIMIT_KEY, AuthConst.TOKEN_LIMIT_VALUE));
        // 登录成功
        LoginResult loginResult = new LoginResult()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(token)
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<>().success(loginResult);
    }

    @PostMapping("userLoginEncrypt")
    @AccessControl
    @Operation(summary = "用户登录(加密)", description = "Anon@将userLogin接口数据,做AES加密作为body的值")
    @LogOperation(value = "用户登录(加密)", type = "login")
    @ApiOperationSupport(order = 110)
    public Result<?> userLoginEncrypt(@Validated @RequestBody EncryptForm encryptForm) {
        LoginForm form = SignUtils.decodeAES(encryptForm.getBody(), Const.AES_KEY, LoginForm.class);
        return ((AuthController) AopContext.currentProxy()).userLogin(form);
    }

    @PostMapping("userLogout")
    @Operation(summary = "用户登出")
    @LogOperation(value = "用户登出", type = "logout")
    @ApiOperationSupport(order = 120)
    public Result<?> userLogout() {
        String token = HttpContextUtils.getRequestParameter(authProps.getTokenHeaderKey());
        tokenService.deleteToken(token);
        return new Result<>();
    }

    @PostMapping("userInfo")
    @Operation(summary = "用户信息")
    @ApiOperationSupport(order = 150)
    public Result<?> userInfo() {
        UserEntity user = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);

        UserDTO data = ConvertUtils.sourceToTarget(user, UserDTO.class);
        // todo 补上用户的其他信息
        // data.setRoleCodes(userService.getUserRoleCodes());
        return new Result<>().success(data);
    }

    @PostMapping("userChangePassword")
    @Operation(summary = "用户修改密码")
    @LogOperation("用户修改密码")
    @ApiOperationSupport(order = 160)
    public Result<?> userChangePassword(@Validated @RequestBody ChangePasswordForm form) {
        String tenantCode = ShiroUtils.getUserTenantCode();
        // 先校验密码复杂度
        JSONObject paramsContent = paramsService.getContentObject(null, tenantCode, null, UcConst.PARAMS_CODE_LOGIN, JSONObject.class, null);
        AssertUtils.isNull(paramsContent, "配置信息为空");
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
        tokenService.deleteByUserIdList(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("userResetPassword")
    @AccessControl
    @Operation(summary = "用户重置密码(帐号找回)")
    @LogOperation("用户重置密码(帐号找回)")
    @ApiOperationSupport(order = 164)
    public Result<?> userResetPassword(@Validated @RequestBody ChangePasswordByMailCodeForm form) {
        String tenantCode = ShiroUtils.getUserTenantCode();
        // 先校验密码复杂度
        JSONObject paramsContent = paramsService.getContentObject(null, tenantCode, null, UcConst.PARAMS_CODE_LOGIN, JSONObject.class, null);
        AssertUtils.isNull(paramsContent, "配置信息为空");
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(paramsContent.getStr("passwordRegExp")) && !ReUtil.isMatch(paramsContent.getStr("passwordRegExp"), form.getPassword()), ErrorCode.ERROR_REQUEST, paramsContent.getStr("passwordRegError", "密码不符合规则"));
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验短信
        MsgLogBody lastSmsLog = msgService.getLatestByTplCode(null, "CODE_LOGIN", form.getMailTo());
        AssertUtils.isTrue(lastSmsLog == null || !form.getSmsCode().equalsIgnoreCase(lastSmsLog.getContentParams().getStr("code")), ErrorCode.ERROR_REQUEST, "验证码错误");
        // 验证码正确,校验过期时间
        AssertUtils.isTrue(lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date()), ErrorCode.ERROR_REQUEST, "验证码已过期");
        // 将短信消费掉
        msgService.consumeLog(lastSmsLog.getId());
        // 更新密码
        userService.updatePassword(data.getId(), form.getPassword());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIdList(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("userChangePasswordEncrypt")
    @Operation(summary = "用户修改密码(加密)")
    @LogOperation("用户修改密码(加密)")
    @ApiOperationSupport(order = 170)
    public Result<?> userChangePasswordEncrypt(@Validated @RequestBody EncryptForm encryptForm) {
        ChangePasswordForm form = SignUtils.decodeAES(encryptForm.getBody(), Const.AES_KEY, ChangePasswordForm.class);
        return ((AuthController) AopContext.currentProxy()).userChangePassword(form);
    }

    @PostMapping("userMenuScope")
    @Operation(summary = "用户权限范围", description = "返回包括菜单、路由、权限、角色等所有内容")
    @ApiOperationSupport(order = 200)
    public Result<MenuScopeResult> userMenuScope(@Validated @RequestBody MenuScopeForm form) {
        ShiroUser user = ShiroUtils.getUser();
        // 过滤出其中显示菜单
        List<TreeNode<Long>> menuList = new ArrayList<>();
        // 过滤出其中路由菜单
        List<MenuResult> urlList = new ArrayList<>();
        // 过滤出其中的权限
        Set<String> permissions = new HashSet<>();
        // 获取该用户所有menu
        menuService.getListByUser(user.getType(), user.getTenantCode(), user.getId(), null, null).forEach(menu -> {
            if (menu.getShowMenu() == 1 && menu.getType() == UcConst.MenuTypeEnum.MENU.value()) {
                // 菜单需要显示 && 菜单类型为菜单
                menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create().set("icon", menu.getIcon()).set("url", menu.getUrl()).set("urlNewBlank", menu.getUrlNewBlank())));
            }
            if (StrUtil.isNotBlank(menu.getUrl())) {
                urlList.add(ConvertUtils.sourceToTarget(menu, MenuResult.class));
            }
            if (form.isPermissions() && StrUtil.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StrUtil.splitTrim(menu.getPermissions(), ','));
            }
        });
        // 将菜单列表转成菜单树
        List<Tree<Long>> menuTree = TreeNodeUtils.buildIdTree(menuList);
        MenuScopeResult result = new MenuScopeResult()
                .setMenuTree(menuTree)
                .setUrlList(urlList);
        if (form.isPermissions()) {
            result.setPermissions(permissions);
        }
        if (form.isRoleCodes()) {
            // 获取角色列表
            Set<String> roles = userService.getUserRoleCodes(user);
            result.setRoleCodes(roles);
        }
        return new Result<MenuScopeResult>().success(result);
    }

    @PostMapping("userMenuTree")
    @Operation(summary = "用户菜单树", description = "用户左侧显示菜单")
    @ApiOperationSupport(order = 230)
    public Result<?> userMenuTree() {
        ShiroUser user = ShiroUtils.getUser();
        List<TreeNode<Long>> menuList = new ArrayList<>();
        // 获取该用户所有menu, 菜单需要显示 && 菜单类型为菜单
        menuService.getListByUser(user.getType(), user.getTenantCode(), user.getId(), UcConst.MenuTypeEnum.MENU.value(), 1)
                .forEach(menu -> menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create().set("icon", menu.getIcon()).set("url", menu.getUrl()).set("urlNewBlank", menu.getUrlNewBlank()))));
        List<Tree<Long>> menuTree = TreeNodeUtils.buildIdTree(menuList);
        return new Result<>().success(menuTree);
    }

    @PostMapping("userPermissions")
    @Operation(summary = "用户授权编码", description = "用户具备的权限,可用于按钮等的控制")
    @ApiOperationSupport(order = 240)
    public Result<?> userPermissions() {
        ShiroUser user = ShiroUtils.getUser();
        Set<String> set = userService.getUserPermissions(user);

        return new Result<>().success(set);
    }

    @PostMapping("userRoles")
    @Operation(summary = "用户角色id", description = "用户具备的角色,可用于按钮等的控制")
    @ApiOperationSupport(order = 250)
    public Result<?> userRoles() {
        ShiroUser user = ShiroUtils.getUser();
        Set<String> set = userService.getUserRoles(user);

        return new Result<>().success(set);
    }

    @PostMapping("userRoleCodes")
    @Operation(summary = "用户角色编码", description = "用户具备的角色,可用于按钮等的控制")
    @ApiOperationSupport(order = 250)
    public Result<?> userRoleCodes() {
        ShiroUser user = ShiroUtils.getUser();
        Set<String> set = userService.getUserRoleCodes(user);

        return new Result<>().success(set);
    }

}

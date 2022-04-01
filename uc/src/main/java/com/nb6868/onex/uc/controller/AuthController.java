package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.*;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.EncryptForm;
import com.nb6868.onex.common.pojo.LoginResult;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.JacksonUtils;
import com.nb6868.onex.common.util.TreeUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.CaptchaForm;
import com.nb6868.onex.uc.dto.MenuDTO;
import com.nb6868.onex.uc.dto.MenuTreeDTO;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.MenuEntity;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/uc/auth/")
@Validated
@Api(tags = "用户授权", position = 10)
public class AuthController {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MenuService menuService;

    @PostMapping("captcha")
    @AccessControl
    @ApiOperation(value = "图形验证码(base64)", notes = "验证时需将uuid和验证码内容一起提交")
    @ApiOperationSupport(order = 10)
    public Result<?> captcha(@Validated @RequestBody CaptchaForm form) {
        String uuid = IdUtil.fastSimpleUUID();
        // 随机arithmetic/spec
        Captcha captcha = captchaService.createCaptcha(uuid, form.getWidth(), form.getHeight(), RandomUtil.randomEle(new String[]{"arithmetic", "spec"}));
        // 将uuid和图片base64返回给前端
        return new Result<>().success(Dict.create().set("uuid", uuid).set("image", captcha.toBase64()));
    }

    @GetMapping("getLoginSettings")
    @ApiOperation("获得登录设置")
    @AccessControl
    @ApiOperationSupport(order = 20)
    public Result<?> getLoginSettings(@RequestParam(required = false, defaultValue = "admin", name = "登录设置") String type) {
        AuthProps.Settings loginSettings = authProps.getSettings().get(type);
        AssertUtils.isNull(loginSettings, "未定义该类型");

        return new Result<>().success(loginSettings);
    }

    @GetMapping("getLoginConfig")
    @ApiOperation("获得登录配置")
    @AccessControl
    @ApiOperationSupport(order = 30)
    public Result<?> getLoginConfig(@RequestParam(required = false, defaultValue = "ADMIN_USERNAME_PASSWORD", name = "登录配置") String type) {
        AuthProps.Config loginConfig = authProps.getConfigs().get(type);
        AssertUtils.isNull(loginConfig, "未定义该类型");

        return new Result<>().success(loginConfig);
    }

    @PostMapping("login")
    @AccessControl
    @ApiOperation(value = "登录")
    @LogOperation(value = "登录", type = "login")
    @ApiOperationSupport(order = 100)
    public Result<?> login(@Validated(value = {DefaultGroup.class}) @RequestBody LoginForm loginRequest) {
        // 获得登录配置
        AuthProps.Config loginConfig = authService.getLoginConfig(loginRequest.getAuthConfigType());
        AssertUtils.isNull(loginConfig, ErrorCode.UNKNOWN_LOGIN_TYPE);

        UserEntity user = authService.login(loginRequest, loginConfig);

        // 登录成功
        LoginResult loginResult = new LoginResult()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(tokenService.createToken(user, loginConfig))
                .setTokenKey(authProps.getTokenKey());
        return new Result<>().success(loginResult);
    }

    @SneakyThrows
    @PostMapping("loginEncrypt")
    @AccessControl
    @ApiOperation(value = "加密登录")
    @LogOperation(value = "加密登录", type = "login")
    @ApiOperationSupport(order = 101)
    public Result<?> loginEncrypt(@Validated @RequestBody EncryptForm form) {
        // 密文->urldecode->aes解码->原明文->json转实体
        String json = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(URLDecoder.decode(form.getBody(), Charset.defaultCharset()));
        LoginForm loginRequest = JacksonUtils.jsonToPojo(json, LoginForm.class);
        // 效验数据
        ValidatorUtils.validateEntity(loginRequest, DefaultGroup.class);
        return login(loginRequest);
    }

    @GetMapping("menuScope")
    @ApiOperation("权限范围")
    @ApiOperationSupport(order = 110)
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

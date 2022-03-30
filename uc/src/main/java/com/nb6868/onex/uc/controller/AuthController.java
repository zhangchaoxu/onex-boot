package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.crypto.SecureUtil;
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
import com.nb6868.onex.uc.service.AuthService;
import com.nb6868.onex.uc.service.MenuService;
import com.nb6868.onex.uc.service.TokenService;
import com.nb6868.onex.uc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

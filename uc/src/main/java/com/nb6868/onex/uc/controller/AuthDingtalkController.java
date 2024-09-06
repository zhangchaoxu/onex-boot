package com.nb6868.onex.uc.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.AuthConst;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.auth.CodeLoginForm;
import com.nb6868.onex.common.auth.LoginResult;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.DingTalkApi;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.ParamsService;
import com.nb6868.onex.uc.service.RoleUserService;
import com.nb6868.onex.uc.service.TokenService;
import com.nb6868.onex.uc.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uc/auth/dingtalk/")
@AccessControl
@Validated
@Tag(name = "用户钉钉登录")
@Slf4j
public class AuthDingtalkController {

    @Autowired
    AuthProps authProps;
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @Autowired
    ParamsService paramsService;
    @Autowired
    RoleUserService roleUserService;

    @PostMapping("userLoginByCode")
    @AccessControl
    @Operation(summary = "钉钉免密code登录", description = "Anon")
    @LogOperation(value = "钉钉免密code登录", type = "login")
    @ApiOperationSupport(order = 300)
    public Result<?> userLoginByCode(@Validated(value = {DefaultGroup.class}) @RequestBody CodeLoginForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]对应的登录配置");
        AssertUtils.isTrue(StrUtil.hasBlank(loginParams.getStr("appId"), loginParams.getStr("appSecret")), "登录配置缺少appId和appSecret信息");

        ApiResult<String> userAccessToken = DingTalkApi.getUserAccessToken(loginParams.getStr("appId"), loginParams.getStr("appSecret"), form.getCode());
        AssertUtils.isTrue(userAccessToken.isSuccess(), userAccessToken.getMsg());
        ApiResult<JSONObject> userContact = DingTalkApi.getUserContact(userAccessToken.getData(), "me");
        AssertUtils.isTrue(userContact.isSuccess(), userContact.getMsg());
        ApiResult<JSONObject> userIdResponse = DingTalkApi.getUserIdByUnionid(userAccessToken.getData(), userContact.getData().getStr("unionId"));
        AssertUtils.isTrue(userIdResponse.isSuccess(), userIdResponse.getMsg());
        // 封装自己的业务逻辑,比如用userId去找用户
        UserEntity user = userService.query().eq("oauth_userid", userIdResponse.getData().getStr("userid")).last(Const.LIMIT_ONE).one();
        if (user == null) {
            // 不存在
            if (loginParams.getBool("autoCreateUserEnable", false)) {
                // 自动创建用户
                user = new UserEntity();
                user.setUsername(userContact.getData().getStr("nick"));
                user.setRealName(userContact.getData().getStr("nick"));
                user.setPassword(DigestUtil.bcrypt(userIdResponse.getData().getStr("userid")));
                user.setPasswordRaw(PasswordUtils.aesEncode(userIdResponse.getData().getStr("userid"), Const.AES_KEY));
                user.setOauthUserid(userIdResponse.getData().getStr("userid"));
                user.setOauthInfo(JSONUtil.parseObj(userContact.getData()));
                user.setMobile(userContact.getData().getStr("mobile"));
                user.setAvatar(userContact.getData().getStr("avatarUrl"));
                user.setType(UcConst.UserTypeEnum.DEPT_ADMIN.value());
                user.setState(UcConst.UserStateEnum.ENABLED.value());
                user.setTenantCode(form.getTenantCode());
                AssertUtils.isTrue(userService.hasDuplicated(null, "username", user.getUsername()), ErrorCode.ERROR_REQUEST, "用户名已存在");
                // AssertUtils.isTrue(userService.hasDuplicated(null, "mobile", user.getMobile()), ErrorCode.ERROR_REQUEST, "手机号已存在");
                userService.save(user);
                // 保存角色关系
                roleUserService.saveOrUpdateByUserIdAndRoleIds(user.getId(), loginParams.getBeanList("autoCreateUserRoleIds", Long.class));
            } else {
                return new Result<>().error("用户未注册");
            }
        }
        // 判断用户是否存在
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
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

}

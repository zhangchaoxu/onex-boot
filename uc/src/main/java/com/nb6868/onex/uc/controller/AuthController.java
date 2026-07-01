package com.nb6868.onex.uc.controller;

import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.Const;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.AuthConst;
import com.nb6868.onex.common.auth.AuthProps;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgLogBody;
import com.nb6868.onex.common.msg.MsgSendReq;
import com.nb6868.onex.common.msg.MsgTplBody;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.util.HttpContextUtils;
import com.nb6868.onex.common.util.PasswordUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.CaptchaRes;
import com.nb6868.onex.uc.dto.LoginRes;
import com.nb6868.onex.uc.dto.TianaiCaptchaTrackReq;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController("UcAuth")
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
    ParamsService paramsService;
    @Autowired
    AuthService authService;
    @Autowired
    BaseMsgService msgService;

    @PostMapping("captcha")
    // @AccessControl
    @Operation(summary = "图形验证码(base64)", description = "Anon")
    public Result<CaptchaRes> captcha(@Validated @RequestBody BaseReq req) {
        // 获得登录验证码配置,设置默认杜绝空信息
        JSONObject captchaParams = paramsService.getSystemPropsObject("CAPTCHA_LOGIN", JSONObject.class, new JSONObject());
        // uuid是用来存和后续对比图片验证码的
        String uuid = IdUtil.fastSimpleUUID();
        // 生成图片，并将uuid和图片内容作为kv存入缓存
        String captchaBase64 = captchaService.createCaptchaBase64(uuid,
                captchaParams.getStr("type", "circle"),
                captchaParams.getStr("randomBase", "0123456789"),
                captchaParams.getInt("randomLength", 4),
                captchaParams.getInt("width", 110),
                captchaParams.getInt("height", 40));
        // 将uuid和图片base64返回给前端
        CaptchaRes captchaRes = new CaptchaRes().setUuid(uuid).setImage(captchaBase64);
        return new Result<CaptchaRes>().success(captchaRes);
    }

    @PostMapping("createTACaptcha")
    // @AccessControl
    @Operation(summary = "生成TAC验证码", description = "Anon")
    public Result<?> createTACaptcha(@Validated @RequestBody BaseReq req) {
        // 获得登录验证码配置,设置默认杜绝空信息
        JSONObject captchaParams = paramsService.getSystemPropsObject("TAC_CAPTCHA", JSONObject.class, new JSONObject());
        List<String> captchaTypeList = captchaParams.getBeanList("captchaType", String.class);
        captchaTypeList = CollUtil.defaultIfEmpty(captchaTypeList, Arrays.asList(CaptchaTypeConstant.SLIDER, CaptchaTypeConstant.WORD_IMAGE_CLICK));
        ApiResponse<ImageCaptchaVO> res = captchaService.createTACCaptcha(RandomUtil.randomEle(captchaTypeList));
        return new Result<>()
                // 按照前端要求成功的时候传回code=200
                .setCode(res.getCode())
                .setMsg(res.getMsg())
                .setData(res.getData());
    }

    @PostMapping("matchAdaptiveCaptcha")
    // @AccessControl
    @Operation(summary = "验证行为验证码", description = "Anon")
    public Result<?> matchingTACCaptcha(@Validated @RequestBody TianaiCaptchaTrackReq req) {
        ApiResponse<?> res = captchaService.matchingTACCaptcha(req.getId(), req.getData());
        return new Result<>()
                // 按照前端要求成功的时候传回code=200
                .setCode(res.getCode())
                .setMsg(res.getMsg())
                .setData(res.getData());
    }

    @PostMapping("userLoginByUsernamePassword")
    // @AccessControl
    @Operation(summary = "用户账号密码登录", description = "Anon")
    @LogOperation(value = "用户账号密码登录", type = "login")
    public Result<LoginRes> userLoginByUsernamePassword(@Validated @RequestBody LoginByUsernamePasswordReq req) {
        // 检查密码不为空
        AssertUtils.isTrue(StrUtil.isAllBlank(req.getPassword(), req.getPasswordEncrypted(), "密码不能为空"));
        // 获得对应登录类型的登录参数,并且设置默认类型
        JSONObject loginParams = paramsService.getSystemPropsJson(StrUtil.blankToDefault(req.getType(), "ADMIN_USERNAME_PASSWORD"));
        AssertUtils.isNull(loginParams, "缺少登录配置");
        // 验证验证码
        if (loginParams.getBool("captcha", false)) {
            // 图形验证码
            authService.checkCaptcha(req, loginParams.getStr("magicCaptcha"));
        } else if (loginParams.getBool("captchaAliyun", false)) {
            // 阿里云验证码
            JSONObject captchaParams = paramsService.getSystemPropsJson("LOGIN_CAPTCHA_ALIYUN");
            AssertUtils.isNull(captchaParams, "缺少阿里云验证码配置");
            authService.checkCaptchaAliyun(req, captchaParams);
        } else if (loginParams.getBool("captchaTAC", false)) {
            // TAC验证码 https://github.com/dromara/tianai-captcha
            authService.checkTACCaptcha(req);
        }
        // 先从加密密码中解密获取，若无则从明文密码获取
        String passwordPlaintext = StrUtil.isNotBlank(req.getPasswordEncrypted()) ? PasswordUtils.aesDecode(req.getPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY)) : req.getPassword();
        AssertUtils.isEmpty(passwordPlaintext, ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 执行登录操作
        UserEntity user = authService.loginByUsernamePassword(req.getTenantCode(), req.getUsername(), passwordPlaintext, loginParams);
        // 创建token
        String token = tokenService.createToken(user,
                loginParams.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE),
                loginParams.getStr("type"),
                loginParams.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE),
                loginParams.getInt(AuthConst.TOKEN_EXPIRE_KEY, AuthConst.TOKEN_EXPIRE_VALUE),
                loginParams.getInt(AuthConst.TOKEN_LIMIT_KEY, AuthConst.TOKEN_LIMIT_VALUE));
        // 登录成功
        LoginRes loginResult = new LoginRes()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(token)
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<LoginRes>().success(loginResult);
    }

    @PostMapping("userLoginByMobileSms")
    // @AccessControl
    @Operation(summary = "手机验证码登录", description = "Anon")
    @LogOperation(value = "手机验证码登录", type = "login")
    public Result<LoginRes> userLoginByMobileSms(@Validated @RequestBody LoginByMobileSmsReq req) {
        // 获得对应登录类型的登录参数,并且设置默认类型
        JSONObject loginParams = paramsService.getSystemPropsJson(StrUtil.blankToDefault(req.getType(), "ADMIN_MOBILE_SMS"));
        AssertUtils.isNull(loginParams, "缺少登录配置");
        // 验证验证码
        if (loginParams.getBool("captcha", false)) {
            authService.checkCaptcha(req, loginParams.getStr("magicCaptcha"));
        } else if (loginParams.getBool("captchaAliyun", false)) {
            // 阿里云验证码
            JSONObject captchaParams = paramsService.getSystemPropsJson("LOGIN_CAPTCHA_ALIYUN");
            AssertUtils.isNull(captchaParams, "缺少阿里云验证码配置");
            authService.checkCaptchaAliyun(req, captchaParams);
        } else if (loginParams.getBool("captchaTAC", false)) {
            // TAC验证码 https://github.com/dromara/tianai-captcha
            authService.checkTACCaptcha(req);
        }
        // 执行登录操作
        UserEntity user = authService.loginByMobileSms(req.getTenantCode(), req.getMobile(), req.getSms(), loginParams);
        // 创建token
        String token = tokenService.createToken(user,
                loginParams.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE),
                loginParams.getStr("type"),
                loginParams.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE),
                loginParams.getInt(AuthConst.TOKEN_EXPIRE_KEY, AuthConst.TOKEN_EXPIRE_VALUE),
                loginParams.getInt(AuthConst.TOKEN_LIMIT_KEY, AuthConst.TOKEN_LIMIT_VALUE));
        // 登录成功
        LoginRes loginResult = new LoginRes()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(token)
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<LoginRes>().success(loginResult);
    }

    /*@PostMapping("userLoginByCode")
    // @AccessControl
    @Operation(summary = "授权code登录,如钉钉", description = "Anon")
    @LogOperation(value = "授权code登录", type = "login")
    public Result<LoginRes> userLoginByCode(@Validated @RequestBody LoginByCodeReq req) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(StrUtil.blankToDefault(req.getType(), "ADMIN_DINGTALK_CODE"));
        AssertUtils.isNull(loginParams, "缺少登录配置");
        AssertUtils.isTrue(StrUtil.hasBlank(loginParams.getStr("appId"), loginParams.getStr("appSecret")), "登录配置缺少appId和appSecret信息");
        // 调用接口获取token
        ApiResult<String> userAccessToken = DingTalkApi.getUserAccessToken(loginParams.getStr("appId"), loginParams.getStr("appSecret"), req.getCode());
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
                if (StrUtil.isNotBlank(authProps.getPasswordStoreKey())) {
                    user.setPasswordRaw(PasswordUtils.aesEncode(userIdResponse.getData().getStr("userid"), authProps.getPasswordStoreKey()));
                }
                user.setOauthUserid(userIdResponse.getData().getStr("userid"));
                user.setOauthInfo(JSONUtil.parseObj(userContact.getData()));
                user.setMobile(userContact.getData().getStr("mobile"));
                user.setAvatar(userContact.getData().getStr("avatarUrl"));
                user.setType(UcConst.UserTypeEnum.DEPT_ADMIN.getCode());
                user.setState(UcConst.UserStateEnum.ENABLED.getCode());
                user.setTenantCode(req.getTenantCode());
                AssertUtils.isTrue(userService.hasDuplicated(null, "username", user.getUsername()), ErrorCode.ERROR_REQUEST, "用户名已存在");
                // AssertUtils.isTrue(userService.hasDuplicated(null, "mobile", user.getMobile()), ErrorCode.ERROR_REQUEST, "手机号已存在");
                userService.save(user);
                // 保存角色关系
                roleUserService.updateByUserIdAndRoleIds(user.getId(), loginParams.getBeanList("autoCreateUserRoleIds", Long.class), UcConst.RoleUserTypeEnum.DEFAULT.getCode());
            } else {
                return new Result<LoginRes>().error("用户未注册");
            }
        }
        // 判断用户是否存在
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.getCode(), ErrorCode.ACCOUNT_DISABLE);
        // 创建token
        String token = tokenService.createToken(user,
                loginParams.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE),
                loginParams.getStr("type"),
                loginParams.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE),
                loginParams.getInt(AuthConst.TOKEN_EXPIRE_KEY, AuthConst.TOKEN_EXPIRE_VALUE),
                loginParams.getInt(AuthConst.TOKEN_LIMIT_KEY, AuthConst.TOKEN_LIMIT_VALUE));
        // 登录成功
        LoginRes loginResult = new LoginRes()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(token)
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<LoginRes>().success(loginResult);
    }*/

    @PostMapping("sendMsgCode")
    // @AccessControl
    @Operation(summary = "发送验证码消息", description = "Anon")
    @LogOperation("发送验证码消息")
    public Result<?> sendMsgCode(@Validated @RequestBody MsgSendReq req) {
        MsgTplBody mailTpl = msgService.getTplByCode(req.getTenantCode(), req.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");
        AssertUtils.isNull(mailTpl.getParams(), ErrorCode.ERROR_REQUEST, "消息模板未做参数配置");
        // 验证验证码
        if (mailTpl.getParams().getBool("captcha", false)) {
            authService.checkCaptcha(req, mailTpl.getParams().getStr("magicCaptcha"));
        } else if (mailTpl.getParams().getBool("captchaAliyun", false)) {
            // 阿里云验证码
            JSONObject captchaParams = paramsService.getSystemPropsJson("SMS_CAPTCHA_ALIYUN");
            AssertUtils.isNull(captchaParams, "缺少阿里云验证码配置");
            authService.checkCaptchaAliyun(req, captchaParams);
        }
        if (mailTpl.getParams().getBool("verifyUserExist", false)) {
            // 是否先验证用户是否存在
            UserEntity user = userService.getByMobile(req.getTenantCode(), req.getMailTo());
            AssertUtils.isNull(user, StrUtil.format("手机号[{}]未找到关联的用户", req.getMailTo()));
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.getCode(), ErrorCode.ACCOUNT_DISABLE);
        }
        // 结果标记
        boolean flag = msgService.sendMail(req);
        if (flag) {
            return new Result<>().success("短信发送成功", null);
        } else {
            return new Result<>().error("短信发送失败");
        }
    }

    @PostMapping("userLogout")
    @Operation(summary = "用户登出")
    @LogOperation(value = "用户登出", type = "logout")
    public Result<?> userLogout(@Validated @RequestBody BaseReq req) {
        String token = HttpContextUtils.getRequestParameter(authProps.getTokenHeaderKey());
        tokenService.deleteToken(token);
        return new Result<>();
    }

    @PostMapping("userResetPassword")
    // @AccessControl
    @Operation(summary = "用户重置密码(帐号找回)")
    @LogOperation("用户重置密码(帐号找回)")
    public Result<?> userResetPassword(@Validated @RequestBody ChangePasswordByMailCodeReq form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]登录配置");
        // 先对密码做解密
        String newPasswordPlaintext = PasswordUtils.aesDecode(form.getNewPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(loginParams.getStr("passwordRegExp")) && !ReUtil.isMatch(loginParams.getStr("passwordRegExp"), newPasswordPlaintext), ErrorCode.ERROR_REQUEST, loginParams.getStr("passwordRegError", "密码不符合规则"));
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
        userService.updatePassword(data.getId(), newPasswordPlaintext, authProps.getPasswordStoreKey());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIdList(Collections.singletonList(data.getId()));
        return new Result<>();
    }

}

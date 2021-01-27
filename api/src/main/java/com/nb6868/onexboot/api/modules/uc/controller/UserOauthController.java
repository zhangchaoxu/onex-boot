package com.nb6868.onexboot.api.modules.uc.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.nb6868.onexboot.api.common.annotation.AccessControl;
import com.nb6868.onexboot.api.common.annotation.LogLogin;
import com.nb6868.onexboot.api.common.annotation.LogOperation;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dto.*;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserOauthEntity;
import com.nb6868.onexboot.api.modules.uc.service.TokenService;
import com.nb6868.onexboot.api.modules.uc.service.UserOauthService;
import com.nb6868.onexboot.api.modules.uc.service.UserService;
import com.nb6868.onexboot.api.modules.uc.wx.WxApiService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.PageData;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.AliSignUtils;
import com.nb6868.onexboot.common.util.ConvertUtils;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.PasswordUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;
import com.nb6868.onexboot.common.validator.group.AddGroup;
import com.nb6868.onexboot.common.validator.group.DefaultGroup;
import com.nb6868.onexboot.common.validator.group.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("UserOauth")
@RequestMapping("uc/userOauth")
@Validated
@Api(tags = "第三方用户")
public class UserOauthController {

    @Autowired
    UserOauthService userOauthService;
    @Autowired
    UserService userService;
    @Autowired
    ParamService paramService;
    @Autowired
    TokenService tokenService;
    @Autowired
    WxApiService wxApiService;

    @GetMapping("list")
    @ApiOperation("列表")
    @RequiresPermissions("uc:userOauth:list")
    public Result<?> list(@ApiIgnore @RequestParam Map<String, Object> params) {
        List<UserOauthDTO> list = userOauthService.listDto(params);

        return new Result<>().success(list);
    }

    @GetMapping("page")
    @ApiOperation("分页")
    @RequiresPermissions("uc:userOauth:page")
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<UserOauthDTO> page = userOauthService.pageDto(params);

        return new Result<>().success(page);
    }

    @GetMapping("info")
    @ApiOperation("信息")
    @RequiresPermissions("uc:userOauth:info")
    public Result<?> info(@RequestParam @NotNull(message = "{id.require}") Long id) {
        UserOauthDTO data = userOauthService.getDtoById(id);
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<UserOauthDTO>().success(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("uc:userOauth:save")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody UserOauthDTO dto) {
        userOauthService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("uc:userOauth:update")
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody UserOauthDTO dto) {
        userOauthService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("uc:userOauth:delete")
    public Result<?> delete(@NotNull(message = "{id.require}") @RequestParam Long id) {
        userOauthService.logicDeleteById(id);

        return new Result<>();
    }

    @DeleteMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("uc:userOauth:deleteBatch")
    public Result<?> deleteBatch(@NotEmpty(message = "{ids.require}") @RequestBody List<Long> ids) {
        userOauthService.logicDeleteByIds(ids);

        return new Result<>();
    }

    /**
     * 微信小程序Oauth授权登录
     */
    @PostMapping("/oauthWxMaLoginByCodeAndUserInfo")
    @ApiOperation("Oauth授权登录")
    @LogLogin
    @AccessControl
    public Result<?> oauthWxMaLoginByCodeAndUserInfo(@Validated @RequestBody OauthWxMaLoginByCodeAndUserInfoRequest request) throws WxErrorException {
        LoginTypeConfig loginChannelCfg = paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + "WECHAT_MA_USER_INFO", LoginTypeConfig.class);
        AssertUtils.isNull(loginChannelCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

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
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginChannelCfg));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

    /**
     * Oauth授权登录
     */
    @PostMapping("/wxMaLoginByCode")
    @ApiOperation("Oauth微信小程序授权登录")
    @LogLogin
    @AccessControl
    public Result<?> oauthWxMaLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) throws WxErrorException {
        LoginTypeConfig loginChannelCfg = paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + "WECHAT_MA_CODE", LoginTypeConfig.class);
        AssertUtils.isNull(loginChannelCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

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
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginChannelCfg));
        kv.set("user", ConvertUtils.sourceToTarget(user, UserDTO.class));
        return new Result<>().success(kv);
    }

    /**
     * Oauth授权登录
     */
    @PostMapping("/wxMaLoginByPhone")
    @ApiOperation("Oauth微信小程序手机号授权登录")
    @LogLogin
    @AccessControl
    public Result<?> wxMaLoginByPhone(@Validated @RequestBody OauthWxMaLoginByCodeAndPhone request) throws WxErrorException {
        LoginTypeConfig loginChannelCfg = paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + "WECHAT_MA_PHONE", LoginTypeConfig.class);
        AssertUtils.isNull(loginChannelCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

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
            user.setStatus(UcConst.UserStatusEnum.ENABLED.value());
            user.setType(UcConst.UserTypeEnum.USER.value());
            userService.save(user);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginChannelCfg));
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
    @AccessControl
    public Result<?> dingtalkLoginByCode(@Validated @RequestBody OauthLoginByCodeRequest request) {
        LoginTypeConfig loginChannelCfg = paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + "ADMIN_DINGTALK_SCAN", LoginTypeConfig.class);
        AssertUtils.isNull(loginChannelCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 1. 根据sns临时授权码获取用户信息
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = new HashMap();
        requestBody.put("tmp_auth_code", request.getCode());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = AliSignUtils.signature(timestamp, "", "HmacSHA256");
        String result = restTemplate.postForObject("https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey={1}&timestamp={2}&signature={3}",
                requestBody, String.class,
                request.getParamCode(), timestamp, signature);
        Map<String, Object> json = JacksonUtils.jsonToMap(result);
        if ((int) json.get("errcode") == 0) {
            Map<String, String> userInfo = (Map<String, String>)json.get("user_info");
            String nick = (String)userInfo.get("nick");
            String unionid = (String)userInfo.get("unionid");
            String openid = (String)userInfo.get("openid");

        } else {
            return new Result<>().error((String) json.get("errmsg"));
        }

        // todo 钉钉接口处理流程
        return new Result<>();
    }

}

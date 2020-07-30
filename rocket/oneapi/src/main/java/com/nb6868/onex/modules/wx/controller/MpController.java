package com.nb6868.onex.modules.wx.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.common.annotation.LogLogin;
import com.nb6868.onex.modules.log.service.LoginService;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.service.TokenService;
import com.nb6868.onex.modules.uc.service.UserService;
import com.nb6868.onex.modules.wx.config.WxProp;
import com.nb6868.onex.modules.wx.dto.WxMpLoginRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 微信公众号接口
 *
 * @author Binary Wang
 * @author Charles
 */
@RestController
@RequestMapping("/wx/mp")
@Validated
@Api(tags = "微信公众号")
public class MpController {

    @Autowired
    ParamService paramService;
    @Autowired
    LoginService logLoginService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

    @GetMapping("/getJsapiSignature")
    @ApiOperation("获得签名")
    @AnonAccess
    @ApiImplicitParams({@ApiImplicitParam(name = "paramCode", value = "微信配置参数表code", paramType = "query", dataType = "String")})
    public Result<?> getJsapiSignature(@RequestParam String paramCode, @RequestParam String url) {
        // 初始化service
        WxMpService wxService = getWxService(paramCode);
        try {
            WxJsapiSignature wxJsapiSignature = wxService.createJsapiSignature(url);
            return new Result<>().success(wxJsapiSignature);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return new Result<>().error(ErrorCode.WX_API_ERROR);
        }
    }

    @GetMapping("/getOauth2Url")
    @ApiOperation("获得OAuth2地址")
    @AnonAccess
    @ApiImplicitParams({@ApiImplicitParam(name = "paramCode", value = "微信配置参数表code", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "scope", value = "授权范围", paramType = "query", dataType = "String")})
    public Result<?> getOauth2Url(@RequestParam String paramCode, @RequestParam String url,
                                  @RequestParam(required = false, defaultValue = WxConsts.OAuth2Scope.SNSAPI_USERINFO) String scope,
                                  @RequestParam(required = false) String state) {
        WxMpService wxService = getWxService(paramCode);
        String oauth2buildAuthorizationUrl = wxService.oauth2buildAuthorizationUrl(url, scope, state);
        return new Result<>().success(oauth2buildAuthorizationUrl);
    }

    /**
     * Oauth授权 {https://github.com/Wechat-Group/WxJava/wiki/MP_OAuth2%E7%BD%91%E9%A1%B5%E6%8E%88%E6%9D%83}
     */
    @PostMapping("/login")
    @ApiOperation("Oauth授权登录")
    @AnonAccess
    @LogLogin(type = UcConst.LoginTypeEnum.APP_WECHAT)
    public Result<?> login(@Validated @RequestBody WxMpLoginRequest request) {
        // 初始化service
        /*WxMpService wxService = getWxService(request.getParamCode());
        // code登录
        WxMpOAuth2AccessToken oAuth2AccessToken;
        try {
            oAuth2AccessToken = wxService.oauth2getAccessToken(request.getCode());
        } catch (WxErrorException e) {
            e.printStackTrace();
            return new Result<>().error(ErrorCode.WX_API_ERROR, "微信登录失败");
        }
        // 获取微信用户信息
        WxMpUser userInfo;
        try {
            userInfo = wxService.oauth2getUserInfo(oAuth2AccessToken, null);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return new Result<>().error(ErrorCode.WX_API_ERROR, "获取微信用户信息失败");
        }
        // 获得登录配置
        LoginChannelCfg loginChannelCfg = paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + UcConst.LoginTypeEnum.APP_WECHAT.value(), LoginChannelCfg.class, null);
        AssertUtils.isNull(loginChannelCfg, ErrorCode.UNKNOWN_LOGIN_TYPE);

        // 登录用户
        UserDTO user;
        // 保存微信用户
        UserWxEntity userWx = userWxService.saveOrUpdateWxMpUser(wxService.getWxMpConfigStorage().getAppId(), userInfo);
        // 判断是否有绑定user_id
        if (null != userWx.getUserId()) {
            // 关联用户id
            user = userService.getDtoById(userWx.getUserId());
        } else {
            // 未关联用户id,创建用户
            user = new UserDTO();
            user.setStatus(UcConst.UserStatusEnum.ENABLED.value());
            user.setUsername(userInfo.getOpenId());
            user.setNickname(userInfo.getNickname());
            user.setType(UcConst.UserTypeEnum.USER.value());
            user.setMobile(userInfo.getOpenId());
            user.setGender(3);
            user.setPassword(PasswordUtils.encode(userInfo.getOpenId()));
            userService.saveDto(user);
            // 保存wx和user关系
            userWx.setUserId(user.getId());
            userWxService.updateById(userWx);
        }
        if (user == null) {
            // 帐号不存在
            throw new OnexException(ErrorCode.ACCOUNT_NOT_EXIST);
        } else if (user.getStatus() != UcConst.UserStatusEnum.ENABLED.value()) {
            // 帐号锁定
            throw new OnexException(ErrorCode.ACCOUNT_DISABLE);
        }
        // 登录成功
        Kv kv = Kv.init();
        kv.set(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginChannelCfg));
        kv.set("expire", loginChannelCfg.getExpire());
        kv.set("user", user);
        return new Result<>().success(kv);*/
        return null;
    }

    /**
     * 获取微信Service
     * @param paramCode 参数编码
     * @return 微信Service
     */
    private WxMpService getWxService(String paramCode) {
        // 从参数表获取参数配置
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class, null);
        AssertUtils.isNull(wxProp, ErrorCode.WX_CONFIG_ERROR);
        // 初始化service
        WxMpService wxService = new WxMpServiceImpl();
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(wxProp.getAppid());
        config.setSecret(wxProp.getSecret());
        config.setToken(wxProp.getToken());
        config.setAesKey(wxProp.getAesKey());
        wxService.setWxMpConfigStorage(config);
        return wxService;
    }

}

package com.nb6868.onex.modules.wx.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.pojo.Const;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.booster.util.HttpContextUtils;
import com.nb6868.onex.booster.util.MessageUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.modules.log.entity.LoginEntity;
import com.nb6868.onex.modules.log.service.LoginService;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.uc.dto.LoginChannelCfg;
import com.nb6868.onex.modules.uc.dto.UserDTO;
import com.nb6868.onex.modules.uc.service.TokenService;
import com.nb6868.onex.modules.uc.service.UserService;
import com.nb6868.onex.modules.wx.config.WxProp;
import com.nb6868.onex.modules.wx.dto.WxMaLoginRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序接口
 *
 * @author Binary Wang
 * @author Charles
 */
@RestController
@RequestMapping("/wx/ma")
@Validated
@Api(tags = "微信小程序-用户")
public class MaController {

    @Autowired
    ParamService paramService;
    @Autowired
    LoginService logLoginService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    @ApiOperation("登录")
    @AnonAccess
    public Result<?> login(HttpServletRequest httpServletRequest, @Validated @RequestBody WxMaLoginRequest request) {
        // 登录日志
        LoginEntity loginLog = new LoginEntity();
        loginLog.setType(UcConst.LoginTypeEnum.APP_WECHAT.value());
        loginLog.setCreateTime(new Date());
        loginLog.setIp(HttpContextUtils.getIpAddr(httpServletRequest));
        loginLog.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));

        // 登录用户
        UserDTO user = null;
        // 登录结果
        int loginResult = 0;
        // 获得登录配置
        LoginChannelCfg loginConfig = paramService.getContentObject(UcConst.LOGIN_CHANNEL_CFG_PREFIX + UcConst.LoginTypeEnum.APP_WECHAT.value(), LoginChannelCfg.class, null);
        if (null == loginConfig) {
            // 未找到登录配置
            loginResult = ErrorCode.UNKNOWN_LOGIN_TYPE;
        } else {
            // 初始化service
            WxMaService wxService = getWxService(request.getParamCode());
            String sessionKey;
            try {
                WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(request.getCode());
                sessionKey = session.getSessionKey();
            } catch (WxErrorException e) {
                loginResult = ErrorCode.WX_API_ERROR;
                loginLog.setMsg(e.getError().getErrorCode() + ":" + e.getError().getErrorMsg());
                return new Result<>().error(ErrorCode.WX_API_ERROR);
            }

            // 用户信息校验
            if (!wxService.getUserService().checkUserInfo(sessionKey, request.getRawData(), request.getSignature())) {
                // tofix 有时候会失败
                loginLog.setResult(Const.ResultEnum.FAIL.value());
                loginLog.setMsg("校验微信用户信息失败");
                logLoginService.save(loginLog);
                return new Result<>().error(ErrorCode.WX_API_ERROR, "校验微信用户信息失败");
            }
            // 解密获得用户信息
            WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, request.getEncryptedData(), request.getIv());
            // 找到数据库中对应记录
            /*UserWxEntity userWx = userWxService.getByAppIdAndOpenId(userInfo.getWatermark().getAppid(), userInfo.getOpenId());
            if (userWx == null) {
                // 不存在,则新增数据
                userWx = new UserWxEntity();
                userWx.setAppId(userInfo.getWatermark().getAppid());
                userWx.setAppType(WxConst.AppTypeEnum.MA.value());
                userWx.setOpenId(userInfo.getOpenId());

                userWx.setUnionId(userInfo.getUnionId());
                userWx.setAvatarUrl(userInfo.getAvatarUrl());
                userWx.setCity(userInfo.getCity());
                userWx.setCountry(userInfo.getCountry());
                userWx.setProvince(userInfo.getProvince());
                userWx.setGender(Integer.valueOf(userInfo.getGender()));
                userWx.setNickName(userInfo.getNickName());
                userWxService.save(userWx);
            } else {
                // 已存在,则更新数据
                userWx.setUnionId(userInfo.getUnionId());
                userWx.setAvatarUrl(userInfo.getAvatarUrl());
                userWx.setCity(userInfo.getCity());
                userWx.setCountry(userInfo.getCountry());
                userWx.setProvince(userInfo.getProvince());
                userWx.setGender(Integer.valueOf(userInfo.getGender()));
                userWx.setNickName(userInfo.getNickName());
                userWxService.updateById(userWx);
            }
            // 判断是否有绑定user_id
            if (null != userWx.getUserId()) {
                // 关联用户id
                user = userService.getDtoById(userWx.getUserId());
            } else {
                // 未关联用户id
                // 没有该用户，并且需要自动创建用户
                user = new UserDTO();
                user.setStatus(UcConst.UserStatusEnum.ENABLED.value());
                user.setMobile("");
                user.setUsername(userInfo.getNickName());
                user.setNickname(userInfo.getNickName());
                user.setType(UcConst.UserTypeEnum.USER.value());
                user.setGender(3);
                // 密码加密
                user.setPassword(PasswordUtils.encode(userInfo.getOpenId()));
                userService.saveDto(user);
                //保存角色用户关系
                userWx.setUserId(user.getId());
                userWxService.updateById(userWx);
                // 保存成功
                loginResult = 0;
            }
            if (user == null) {
                // 帐号不存在
                loginResult = ErrorCode.ACCOUNT_NOT_EXIST;
            } else if (user.getStatus() != UcConst.UserStatusEnum.ENABLED.value()) {
                // 帐号锁定
                loginResult = ErrorCode.ACCOUNT_DISABLE;
            }*/
        }

        if (loginResult == 0) {
            loginLog.setResult(Const.ResultEnum.SUCCESS.value());
            loginLog.setMsg("ok");
            logLoginService.save(loginLog);
            // 登录成功
            Map<String, Object> map = new HashMap<>(3);
            map.put(UcConst.TOKEN_HEADER, tokenService.createToken(user.getId(), loginConfig));
            map.put("expire", loginConfig.getExpire());
            map.put("user", user);
            return new Result<>().success(map);
        } else {
            loginLog.setResult(Const.ResultEnum.FAIL.value());
            loginLog.setMsg(MessageUtils.getMessage(loginResult));
            logLoginService.save(loginLog);
            // 登录失败
            return new Result<>().error(loginResult);
        }
    }

    @ApiOperation("获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramCode", value = "微信配置参数表code", paramType = "query", dataType = "String")
    })
    @GetMapping("/info")
    public Result<?> info(@RequestParam String paramCode, String sessionKey, String signature, String rawData, String encryptedData, String iv) {
        // 初始化service
        WxMaService wxService = getWxService(paramCode);
        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        return new Result<>().success(userInfo);
    }

    @GetMapping("/phone")
    @ApiOperation("获取用户绑定手机号信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramCode", value = "微信配置参数表code", paramType = "query", dataType = "String")
    })
    public Result<?> phone(@RequestParam String paramCode, String sessionKey, String signature, String rawData, String encryptedData, String iv) {
        // 初始化service
        WxMaService wxService = getWxService(paramCode);
        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        return new Result<>().success(phoneNoInfo);
    }

    @GetMapping("/getJsapiSignature")
    @ApiOperation("获得签名")
    @ApiImplicitParams({@ApiImplicitParam(name = "paramCode", value = "微信配置参数表code", paramType = "query", dataType = "String")})
    public Result<?> getJsapiSignature(@RequestParam String paramCode) {
        // 初始化service
        WxMaService wxService = getWxService(paramCode);
        return new Result<>().success("");
    }

    /**
     * 获取微信Service
     * @param paramCode 参数编码
     * @return 微信Service
     */
    private WxMaService getWxService(String paramCode) {
        // 从参数表获取参数配置
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class, null);
        AssertUtils.isNull(wxProp, ErrorCode.WX_CONFIG_ERROR);
        // 初始化service
        WxMaService wxService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wxProp.getAppid());
        config.setSecret(wxProp.getSecret());
        config.setToken(wxProp.getToken());
        config.setAesKey(wxProp.getAesKey());
        config.setMsgDataFormat(wxProp.getMsgDataFormat());
        wxService.setWxMaConfig(config);
        return wxService;
    }

}

package com.nb6868.onexboot.api.modules.uc.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.nb6868.onexboot.api.common.annotation.AccessControl;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.wx.WxProp;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.validator.AssertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信相关接口
 * 更多使用方法见 {https://github.com/binarywang/weixin-java-mp-demo-springboot}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController("Wx")
@RequestMapping("/uc/wx")
@Validated
@Api(tags = "微信接口")
public class WxController {

    @Autowired
    ParamService paramService;

    @GetMapping("getJsapiSignature")
    @ApiOperation("获得签名")
    @AccessControl("/getJsapiSignature")
    public Result<?> getJsapiSignature(@RequestParam(required = false, defaultValue = "WX_CONFIG") String paramCode, @RequestParam String url) {
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class);
        AssertUtils.isNull(wxProp, ErrorCode.WX_CONFIG_ERROR);

        if ("MP".equalsIgnoreCase(wxProp.getType())) {
            WxMpService wxService = getWxMpService(wxProp);
            try {
                WxJsapiSignature wxJsapiSignature = wxService.createJsapiSignature(url);
                return new Result<>().success(wxJsapiSignature);
            } catch (WxErrorException e) {
                return new Result<>().error(ErrorCode.WX_API_ERROR);
            }
        } else if ("MA".equalsIgnoreCase(wxProp.getType())) {
            WxMaService wxService = getWxMaService(wxProp);
            try {
                WxJsapiSignature wxJsapiSignature = wxService.getJsapiService().createJsapiSignature(url);
                return new Result<>().success(wxJsapiSignature);
            } catch (WxErrorException e) {
                return new Result<>().error(ErrorCode.WX_API_ERROR);
            }
        } else {
            return new Result<>().error("不支持的微信配置类型");
        }
    }

    @GetMapping("getOauth2Url")
    @ApiOperation("获得OAuth2地址")
    @AccessControl("/getOauth2Url")
    public Result<?> getOauth2Url(@RequestParam String paramCode, @RequestParam String url,
                                  @RequestParam(required = false, defaultValue = WxConsts.OAuth2Scope.SNSAPI_USERINFO) String scope,
                                  @RequestParam(required = false) String state) {
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class);
        AssertUtils.isNull(wxProp, ErrorCode.WX_CONFIG_ERROR);

        WxMpService wxService = getWxMpService(wxProp);
        String oauth2buildAuthorizationUrl = wxService.getOAuth2Service().buildAuthorizationUrl(url, scope, state);
        return new Result<>().success(oauth2buildAuthorizationUrl);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public Result<?> info(@RequestParam(name = "微信配置code") String paramCode, String sessionKey, String signature, String rawData, String encryptedData, String iv) {
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class);
        AssertUtils.isNull(wxProp, ErrorCode.WX_CONFIG_ERROR);

        // 初始化service
        WxMaService wxService = getWxMaService(wxProp);
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
    public Result<?> phone(@RequestParam(name = "微信配置参数表code") String paramCode, String sessionKey, String signature, String rawData, String encryptedData, String iv) {
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class);
        AssertUtils.isNull(wxProp, ErrorCode.WX_CONFIG_ERROR);

        // 初始化service
        WxMaService wxService = getWxMaService(wxProp);
        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return new Result<>().error(ErrorCode.WX_API_ERROR, "user check failed");
        }
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        return new Result<>().success(phoneNoInfo);
    }

    /**
     * 获取微信公众号Service
     */
    private WxMaService getWxMaService(WxProp prop) {
        // 初始化service
        WxMaService wxService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(prop.getAppid());
        config.setSecret(prop.getSecret());
        config.setToken(prop.getToken());
        config.setAesKey(prop.getAesKey());
        config.setMsgDataFormat(prop.getMsgDataFormat());
        wxService.setWxMaConfig(config);
        return wxService;
    }

    /**
     * 获取微信小程序Service
     */
    private WxMpService getWxMpService(WxProp prop) {
        // 初始化service
        WxMpService wxService = new WxMpServiceImpl();
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(prop.getAppid());
        config.setSecret(prop.getSecret());
        config.setToken(prop.getToken());
        config.setAesKey(prop.getAesKey());
        wxService.setWxMpConfigStorage(config);
        return wxService;
    }

}

package com.nb6868.onex.modules.uc.controller;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.util.StringUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.modules.sys.service.ParamService;
import com.nb6868.onex.modules.uc.UcConst;
import com.nb6868.onex.modules.wx.config.WxProp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * html页面
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Controller("UcHtml")
@RequestMapping("uc/html")
@Api(tags = "uc html")
public class HtmlController {

    @AnonAccess
    @ApiOperation("绑定微信")
    @GetMapping("wx/bind")
    public String wxBind(ModelMap map, @RequestParam(required = false, defaultValue = UcConst.WX_CFG_MP) String paramCode, @RequestParam(required = false) String code) {
        if (StringUtils.isEmpty(code)) {
            WxMpService wxService = getWxService(paramCode);
            String oauth2buildAuthorizationUrl = wxService.oauth2buildAuthorizationUrl("url", WxConsts.OAuth2Scope.SNSAPI_USERINFO, "state");
            return "redirect:" + oauth2buildAuthorizationUrl;
        } else {
            WxMpService wxService = getWxService(paramCode);
            // code登录
            WxMpOAuth2AccessToken oAuth2AccessToken;
            try {
                oAuth2AccessToken = wxService.oauth2getAccessToken(code);
            } catch (WxErrorException e) {
                return "msg";
            }
        }
        return "uc/wx-bind";
    }

    @Autowired
    ParamService paramService;

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

package com.nb6868.onex.modules.uc.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.sys.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 微信service工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class WxApiService {

    @Autowired
    ParamService paramService;

    public WxProp getWxProp(String paramCode) {
        WxProp wxProp = paramService.getContentObject(paramCode, WxProp.class, null);
        AssertUtils.isNull(wxProp, "微信配置错误");
        return wxProp;
    }

    public WxMaService getWxMaService(String paramCode) {
        WxProp wxProp = getWxProp(paramCode);
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

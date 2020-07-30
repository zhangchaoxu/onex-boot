package com.nb6868.onex.modules.pay.util;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.nb6868.onex.booster.util.JacksonUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.wx.config.WxPayProp;

public class PayUtils {

    /**
     * 通过配置获得微信支付服务
     * @param param 支付配置
     * @return WxPayService
     */
    public static WxPayService getWxPayServiceByParam(String param) {
        WxPayProp wxProp = JacksonUtils.jsonToPojo(param, WxPayProp.class, null);
        AssertUtils.isNull(wxProp, "支付配置参数错误");
        // 初始化service
        WxPayService wxService = new WxPayServiceImpl();
        WxPayConfig config = new WxPayConfig();
        config.setAppId(wxProp.getAppId());
        config.setKeyPath(wxProp.getKeyPath());
        config.setMchKey(wxProp.getMchKey());
        config.setMchId(wxProp.getMchId());
        config.setSubAppId(wxProp.getSubAppId());
        config.setSubMchId(wxProp.getSubMchId());
        config.setTradeType(wxProp.getTradeType());
        wxService.setConfig(config);
        return wxService;
    }

}

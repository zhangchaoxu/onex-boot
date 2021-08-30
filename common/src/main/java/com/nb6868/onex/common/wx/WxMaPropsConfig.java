package com.nb6868.onex.common.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.message.WxMaMessageHandler;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序服务配置
 *
 * @author Charles zhangchaoxu@gmail.com
 * @see https://gitee.com/binary/weixin-java-miniapp-demo/blob/master/src/main/java/com/github/binarywang/demo/wx/miniapp/config/WxMaConfiguration.java
 */
@Slf4j
@Configuration
public class WxMaPropsConfig {

    @Autowired
    WxMaProps properties;

    private static final Map<String, WxMaMessageRouter> routers = new HashMap<>();
    private final static Map<String, WxMaService> maServices = new HashMap<>();

    public static WxMaService getMaService(String code) {
        WxMaService wxService = maServices.get(code);
        if (wxService == null) {
            throw new IllegalArgumentException(String.format("未找到对应code=[%s]的配置", code));
        }

        return wxService;
    }

    public static WxMaMessageRouter getRouter(String code) {
        WxMaMessageRouter messageRouter = routers.get(code);
        if (messageRouter == null) {
            throw new IllegalArgumentException(String.format("未找到对应code=[%s]的配置", code));
        }

        return messageRouter;
    }

    @PostConstruct
    public void init() {
        properties.getConfigs().forEach((s, wxProp) -> {
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(wxProp.getAppid());
            config.setSecret(wxProp.getSecret());
            config.setToken(wxProp.getToken());
            config.setAesKey(wxProp.getAesKey());
            config.setMsgDataFormat(wxProp.getMsgDataFormat());

            WxMaService service = new WxMaServiceImpl();
            service.setWxMaConfig(config);
            routers.put(s, newRouter(service));
            maServices.put(s, service);
        });
    }

    private WxMaMessageRouter newRouter(WxMaService service) {
        final WxMaMessageRouter router = new WxMaMessageRouter(service);
        router.rule().handler(logHandler).next()
                .rule().async(false).content("订阅消息").handler(subscribeMsgHandler).end()
                .rule().async(false).content("文本").handler(textHandler).end()
                .rule().async(false).content("图片").handler(picHandler).end()
                .rule().async(false).content("二维码").handler(qrcodeHandler).end();
        return router;
    }

    private final WxMaMessageHandler subscribeMsgHandler = (wxMessage, context, service, sessionManager) -> {
        service.getMsgService().sendSubscribeMsg(WxMaSubscribeMessage.builder()
                .templateId("此处更换为自己的模板id")
                .data(Collections.singletonList(
                        new WxMaSubscribeMessage.MsgData("keyword1", "339208499")))
                .toUser(wxMessage.getFromUser())
                .build());
        return null;
    };

    private final WxMaMessageHandler logHandler = (wxMessage, context, service, sessionManager) -> {
        log.info("收到消息：" + wxMessage.toString());
        service.getMsgService().sendKefuMsg(WxMaKefuMessage.newTextBuilder().content("收到信息为：" + wxMessage.toJson())
                .toUser(wxMessage.getFromUser()).build());
        return null;
    };

    private final WxMaMessageHandler textHandler = (wxMessage, context, service, sessionManager) -> {
        service.getMsgService().sendKefuMsg(WxMaKefuMessage.newTextBuilder().content("回复文本消息")
                .toUser(wxMessage.getFromUser()).build());
        return null;
    };

    private final WxMaMessageHandler picHandler = (wxMessage, context, service, sessionManager) -> {
        try {
            WxMediaUploadResult uploadResult = service.getMediaService()
                    .uploadMedia("image", "png",
                            ClassLoader.getSystemResourceAsStream("tmp.png"));
            service.getMsgService().sendKefuMsg(
                    WxMaKefuMessage
                            .newImageBuilder()
                            .mediaId(uploadResult.getMediaId())
                            .toUser(wxMessage.getFromUser())
                            .build());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return null;
    };

    private final WxMaMessageHandler qrcodeHandler = (wxMessage, context, service, sessionManager) -> {
        try {
            final File file = service.getQrcodeService().createQrcode("123", 430);
            WxMediaUploadResult uploadResult = service.getMediaService().uploadMedia("image", file);
            service.getMsgService().sendKefuMsg(
                    WxMaKefuMessage
                            .newImageBuilder()
                            .mediaId(uploadResult.getMediaId())
                            .toUser(wxMessage.getFromUser())
                            .build());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return null;
    };
}

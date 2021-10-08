package com.nb6868.onex.common.wechat;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号服务配置
 *
 * @author Charles zhangchaoxu@gmail.com
 * @see {https://gitee.com/binary/weixin-java-mp-demo-springboot/blob/master/src/main/java/com/github/binarywang/demo/wx/mp/config/WxMpConfiguration.java}
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "onex.wechat.mp.enabled", havingValue = "true")
public class WechatMpPropsConfig {

    @Autowired
    WechatMpProps props;

    private static WxMpService mpServices;

    /**
     * 获得服务
     */
    public static WxMpService getService(String code) {
        WxMpService service = mpServices.switchoverTo(code);
        if (service == null) {
            throw new IllegalArgumentException(String.format("未找到对应code=[%s]的配置", code));
        }
        return service;
    }

    @PostConstruct
    public void init() {
        mpServices = new WxMpServiceImpl();
        if (props == null || ObjectUtil.isEmpty(props.getConfigs())) {
            log.info("未配置微信公众号,如有需要可配置到onex.yml或持久化");
            return;
        }
        Map<String, WxMpConfigStorage> configStorages = new HashMap<>();
        props.getConfigs().forEach((s, prop) -> {
            WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(prop.getAppid());
            config.setSecret(prop.getSecret());
            config.setToken(prop.getToken());
            config.setAesKey(prop.getAesKey());
            configStorages.put(s, config);
        });
        mpServices.setMultiConfigStorages(configStorages);
    }
}

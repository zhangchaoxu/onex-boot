package com.nb6868.onex.common.config;

import org.greenrobot.eventbus.EventBus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EventBus
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.eventbus.enable", havingValue = "true")
public class EventBusConfig {

    /**
     * 使用@Bean配置EventBus
     * 以后直接注入使用即可
     */
    @Bean
    public EventBus eventBus() {
        return EventBus.getDefault();
    }

}

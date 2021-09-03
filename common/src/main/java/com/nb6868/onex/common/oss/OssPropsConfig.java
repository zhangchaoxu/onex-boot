package com.nb6868.onex.common.oss;

import cn.hutool.core.util.ObjectUtil;
import com.nb6868.onex.common.config.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "oss.enabled")
@PropertySource(value = "classpath:onex.yml", factory = YamlPropertySourceFactory.class)
public class OssPropsConfig {

    @Autowired
    OssProps props;

    private final static Map<String, AbstractOssService> ossServices = new HashMap<>();

    /**
     * 获得服务
     */
    public static AbstractOssService getService(String code) {
        AbstractOssService service = ossServices.get(code);
        if (service == null) {
            throw new IllegalArgumentException(String.format("未找到对应code=[%s]的配置", code));
        }

        return service;
    }

    @PostConstruct
    public void init() {
        if (props == null || ObjectUtil.isEmpty(props.getConfigs())) {
            log.info("未配置存储信息,如有需要可配置到持久化");
            return;
        }
        props.getConfigs().forEach((s, prop) -> {
            if ("aliyun".equalsIgnoreCase(prop.getType())) {
                ossServices.put(s, new AliyunOssService(prop));
            } else if ("local".equalsIgnoreCase(prop.getType())) {
                ossServices.put(s, new LocalOssService(prop));
            }
        });
    }
}

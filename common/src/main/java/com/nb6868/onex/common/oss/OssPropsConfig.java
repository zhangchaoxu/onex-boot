package com.nb6868.onex.common.oss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
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
        props.getConfigs().forEach((s, prop) -> {
            if ("aliyun".equalsIgnoreCase(prop.getType())) {
                ossServices.put(s, new AliyunOssService(prop));
            } else if ("local".equalsIgnoreCase(prop.getType())) {
                ossServices.put(s, new LocalOssService(prop));
            }
        });
    }
}
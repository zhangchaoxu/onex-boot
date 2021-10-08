package com.nb6868.onex.common.oss;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConditionalOnProperty(name = "onex.oss.enable", havingValue = "true")
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
        if (props == null || ObjectUtil.isEmpty(props.getConfigs())) {
            log.info("未配置存储信息,如有需要可配置到onex.yml或持久化");
            return;
        }
        props.getConfigs().forEach((s, prop) -> {
            if ("aliyun".equalsIgnoreCase(prop.getType())) {
                ossServices.put(s, new AliyunOssService(prop));
                log.info("load config oss aliyun [{}]", s);
            } else if ("local".equalsIgnoreCase(prop.getType())) {
                ossServices.put(s, new LocalOssService(prop));
                log.info("load config oss local [{}]", s);
            } else {
                log.info("load config fail oss [{}] [{}], only support aliyun/local", prop.getType(), s);
            }
        });
    }
}

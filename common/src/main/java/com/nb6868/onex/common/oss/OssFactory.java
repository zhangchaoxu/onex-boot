package com.nb6868.onex.common.oss;

import lombok.extern.slf4j.Slf4j;

/**
 * 资源管理Factory
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class OssFactory {

    public static AbstractOssService build(OssPropsConfig config){
        if ("aliyun".equalsIgnoreCase(config.getType())) {
            return new AliyunOssService(config);
        } else if ("huaweicloud".equalsIgnoreCase(config.getType())) {
            return new HuaweiCloudOssService(config);
        } else if ("local".equalsIgnoreCase(config.getType())) {
            return new LocalOssService(config);
        } else {
            log.info("load config fail oss [{}] [{}], only support aliyun/huaweicloud/local", config.getType(), config);
        }

        return null;
    }

}

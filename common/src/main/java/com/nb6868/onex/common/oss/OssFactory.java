package com.nb6868.onex.common.oss;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源管理Factory
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class OssFactory {

    @SuppressWarnings("unchecked")
    public static AbstractOssService build(OssPropsConfig config){
        if ("aliyun".equalsIgnoreCase(config.getType())) {
            return new AliyunOssService(config);
        } else if ("huaweicloud".equalsIgnoreCase(config.getType())) {
            return new HuaweiCloudOssService(config);
        } else if ("local".equalsIgnoreCase(config.getType())) {
            return new LocalOssService(config);
        } else {
            if (StrUtil.isNotBlank(config.getServiceClassName())) {
                try {
                    Class<AbstractOssService> clazz = (Class<AbstractOssService>) Class.forName(config.getServiceClassName());
                    return ReflectUtil.newInstance(clazz, config);
                } catch (Exception e) {
                    log.error("can not new instance", e);
                    e.printStackTrace();
                }
            }
            log.info("load config fail oss [{}] [{}], only support aliyun/huaweicloud/local", config.getType(), config);
        }

        return null;
    }

}

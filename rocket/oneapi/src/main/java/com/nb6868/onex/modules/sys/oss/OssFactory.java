package com.nb6868.onex.modules.sys.oss;

import com.nb6868.onex.booster.exception.ErrorCode;
import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.booster.util.SpringContextUtils;
import com.nb6868.onex.booster.validator.AssertUtils;
import com.nb6868.onex.modules.sys.service.ParamService;

/**
 * 文件上传Factory
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public final class OssFactory {

    private static ParamService paramsService;

    static {
        OssFactory.paramsService = SpringContextUtils.getBean(ParamService.class);
    }

    public static AbstractOssService build(String paramCode) {
        // 获取云存储配置信息
        OssProp config = paramsService.getContentObject(paramCode, OssProp.class, null);
        AssertUtils.isNull(config, ErrorCode.OSS_CONFIG_ERROR);

        if ("aliyun".equalsIgnoreCase(config.getType())) {
            return new AliyunOssService(config);
        } else if ("local".equalsIgnoreCase(config.getType())) {
            return new LocalOssService(config);
        } else {
            throw new OnexException(ErrorCode.OSS_CONFIG_ERROR);
        }
    }

}

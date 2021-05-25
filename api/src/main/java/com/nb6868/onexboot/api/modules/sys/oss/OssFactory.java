package com.nb6868.onexboot.api.modules.sys.oss;

import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.util.SpringContextUtils;
import com.nb6868.onexboot.common.validator.AssertUtils;

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
        OssProp config = paramsService.getContentObject(paramCode, OssProp.class);
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

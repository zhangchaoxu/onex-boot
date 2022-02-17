package com.nb6868.onex.common.oss;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;

import java.io.File;
import java.io.InputStream;

/**
 * 本地上传
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LocalOssService extends AbstractOssService {

    public LocalOssService(OssProps.Config config) {
        this.config = config;
    }

    @Override
    public InputStream download(String objectKey) {
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        if (localFile.exists()) {
            return IoUtil.toStream(localFile);
        } else {
            return null;
        }
    }

    @Override
    public String upload(String prefix, InputStream inputStream, String fileName) {
        String prefixTotal = StrUtil.isNotEmpty(config.getPrefix()) ? config.getPrefix() : "";
        if (StrUtil.isNotEmpty(prefix)) {
            if (StrUtil.isNotEmpty(prefixTotal)) {
                prefixTotal += "/" + prefix;
            } else {
                prefixTotal = prefix;
            }
        }
        String objectKey = buildUploadPath(prefixTotal, fileName, config.getKeepFileName(), false);
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        if (localFile.exists()) {
            // 文件已存在,则需要对文件重命名
            objectKey = buildUploadPath(prefixTotal, fileName, config.getKeepFileName(), true);
        }
        new FileWriter(localFile).writeFromStream(inputStream, true);
        //FileUtil.copy(inputStream, localFile, true);
        return config.getDomain() + objectKey;
    }

    @Override
    public String generatePresignedUrl(String objectName, long expiration) {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持生成url模式");
    }

    @Override
    public Dict getSts() {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持sts模式");
    }
}

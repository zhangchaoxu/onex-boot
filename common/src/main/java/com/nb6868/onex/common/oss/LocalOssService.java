package com.nb6868.onex.common.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * 本地上传
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LocalOssService extends AbstractOssService {

    public LocalOssService(OssPropsConfig config) {
        this.config = config;
    }

    @Override
    public String upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        File localFile = new File(config.getBucketName() + File.separator + objectKey);
        new FileWriter(localFile).writeFromStream(inputStream, true);
        return config.getDomain() + objectKey;
    }

    @Override
    public InputStream download(String objectKey) {
        File localFile = new File(config.getBucketName() + File.separator + objectKey);
        if (localFile.exists()) {
            return IoUtil.toStream(localFile);
        } else {
            return null;
        }
    }

    @Override
    public String getPresignedUrl(String objectName, Long expiration) {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持生成url模式");
    }

    @Override
    public JSONObject getSts() {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持sts模式");
    }

    @Override
    public boolean isObjectKeyExisted(String bucketName, String objectKey) {
        return FileUtil.exist(bucketName + File.separator +  objectKey);
    }
}

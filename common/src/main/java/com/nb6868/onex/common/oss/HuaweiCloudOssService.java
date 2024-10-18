package com.nb6868.onex.common.oss;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 华为云OBS存储
 * see {<a href="https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_0105.html">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class HuaweiCloudOssService extends AbstractOssService {

    ObsClient s3Client;

    public HuaweiCloudOssService(OssPropsConfig config) {
        this.config = config;
        this.s3Client = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
    }

    @Override
    public String upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        try {
            com.obs.services.model.ObjectMetadata objectMetadata = null;
            if (objectMetadataMap != null && !objectMetadataMap.isEmpty()) {
                objectMetadata = new  com.obs.services.model.ObjectMetadata();
                objectMetadata.setCacheControl(MapUtil.getStr(objectMetadataMap, "CacheControl", "no-cache"));
                objectMetadata.setContentType(MapUtil.getStr(objectMetadataMap, "ContentType"));
                objectMetadata.setContentDisposition(MapUtil.getStr(objectMetadataMap, "ContentDisposition", "inline"));
            }
            s3Client.putObject(config.getBucketName(), objectKey, inputStream);
        } catch (ObsException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            // 关闭ObsClient实例，如果是全局ObsClient实例，可以不在每个方法调用完成后关闭
            // ObsClient在调用ObsClient.close方法关闭后不能再次使用
            if (s3Client != null) {
                try {
                    s3Client.close();
                } catch (IOException e) {
                    log.error("huaweicloud obs close error", e);
                }
            }
        }

        return config.getDomain() + objectKey;
    }

    @Override
    public InputStream download(String objectKey) {
        ObsClient ossClient = null;
        ObsObject ossObject;
        try {
            ossClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
            ossObject = ossClient.getObject(config.getBucketName(), objectKey);
            return ossObject.getObjectContent();
        } catch (ObsException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            // 关闭ObsClient实例，如果是全局ObsClient实例，可以不在每个方法调用完成后关闭
            // ObsClient在调用ObsClient.close方法关闭后不能再次使用
            if (ossClient != null) {
                try {
                    ossClient.close();
                } catch (IOException e) {
                    log.error("huaweicloud obs client close error", e);
                }
            }
        }
    }

    @Override
    public String getPresignedUrl(String objectKey, String method, Long expire) {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "华为云存储暂不支持生成url模式");
    }

    @Override
    public JSONObject getSts() {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "华为云存储暂不支持sts模式");
    }

    @Override
    public boolean isObjectKeyExisted(String bucketName, String objectKey) {
        return s3Client.doesObjectExist(bucketName, objectKey);
    }
}

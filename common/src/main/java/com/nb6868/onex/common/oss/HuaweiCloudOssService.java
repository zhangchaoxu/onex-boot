package com.nb6868.onex.common.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import com.aliyun.oss.OSSException;
import com.nb6868.onex.common.pojo.ApiResult;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
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
    public ApiResult<JSONObject> upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        ApiResult<JSONObject> apiResult = new ApiResult<>();
        try {
            com.obs.services.model.ObjectMetadata objectMetadata = new com.obs.services.model.ObjectMetadata();
            if (objectMetadataMap != null && !objectMetadataMap.isEmpty()) {
                objectMetadata.setCacheControl(MapUtil.getStr(objectMetadataMap, "CacheControl", "no-cache"));
                objectMetadata.setContentType(MapUtil.getStr(objectMetadataMap, "ContentType"));
                objectMetadata.setContentDisposition(MapUtil.getStr(objectMetadataMap, "ContentDisposition", "inline"));
            }
            if (StrUtil.isBlank(objectMetadata.getContentType())) {
                objectMetadata.setContentType(StrUtil.emptyToDefault(FileUtil.getMimeType(objectKey), ContentType.OCTET_STREAM.getValue()));
            }
            PutObjectResult putObjectResult = s3Client.putObject(config.getBucketName(), objectKey, inputStream, objectMetadata);
            JSONObject resultJson = new JSONObject();
            resultJson.set("requestId", putObjectResult.getRequestId());
            resultJson.set("versionId", putObjectResult.getVersionId());
            apiResult.success(resultJson);
        } catch (ObsException e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件上传异常:" + e.getMessage());
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
        return apiResult;
    }

    @Override
    public ApiResult<InputStream> download(String objectKey) {
        ApiResult<InputStream> apiResult = new ApiResult<>();

        ObsClient ossClient = null;
        ObsObject ossObject;
        try {
            ossClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
            ossObject = ossClient.getObject(config.getBucketName(), objectKey);
            apiResult.success(ossObject.getObjectContent());
        } catch (ObsException e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件下载异常:" + e.getMessage());
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
        return apiResult;
    }

    @Override
    public ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire) {
        return new ApiResult<String>().error(ApiResult.ERROR_CODE_EXCEPTION, "huaweicloud oss getPreSignedUrl 未实现");
    }

    @Override
    public ApiResult<Boolean> isObjectKeyExisted(String bucketName, String objectKey) {
        // 给一个默认值，免得出错
        ApiResult<Boolean> apiResult = ApiResult.of(false);
        try {
            apiResult.setData(s3Client.doesObjectExist(bucketName, objectKey));
        } catch (OSSException | com.aliyun.oss.ClientException e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "doesObjectExist exception:" + e.getMessage());
        }
        return apiResult;
    }
}

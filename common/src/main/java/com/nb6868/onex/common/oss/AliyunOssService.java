package com.nb6868.onex.common.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * 阿里云存储
 * see <a href="https://help.aliyun.com/document_detail/32008.html">...</a>
 *
 * 如何配置访问OSS文件时是预览行为
 * see <a href="https://help.aliyun.com/zh/oss/user-guide/how-to-ensure-an-object-is-previewed-when-you-access-the-object">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AliyunOssService extends AbstractOssService {

    OSS s3Client;

    public AliyunOssService(OssPropsConfig config) {
        this.config = config;
        this.s3Client = new OSSClientBuilder().build(config.getEndPoint(), config.getAccessKeyId(), config.getAccessKeySecret());
    }

    @Override
    public ApiResult<JSONObject> upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        ApiResult<JSONObject> apiResult = new ApiResult<>();
        try {
            // 阿里云上传file会自动按照文件后缀确定application/octet-stream
            // InputStream的都是application/octet-stream
            ObjectMetadata objectMetadata = new ObjectMetadata();
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
            resultJson.set("eTag", putObjectResult.getETag());
            resultJson.set("objectKey", objectKey);
            apiResult.success(resultJson);
        } catch (OSSException | com.aliyun.oss.ClientException e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件上传异常:" + e.getMessage());
        } finally {
            s3Client.shutdown();
        }
        return apiResult;
    }

    @Override
    public ApiResult<InputStream> download(String objectKey) {
        ApiResult<InputStream> apiResult = new ApiResult<>();
        OSSObject ossObject = null;
        try {
            ossObject = s3Client.getObject(config.getBucketName(), objectKey);
            apiResult.success(ossObject.getObjectContent());
        } catch (OSSException | com.aliyun.oss.ClientException e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件下载异常:" + e.getMessage());
        } finally {
            if (s3Client != null) {
                s3Client.shutdown();
            }
            if (ossObject != null) {
                try {
                    ossObject.close();
                } catch (IOException e) {
                    log.error("aliyun oss close error", e);
                }
            }
        }
        return apiResult;
    }


    @Override
    public ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire) {
        ApiResult<String> apiResult = new ApiResult<>();
        try {
            // 设置URL过期时间。
            Date expiration = DateUtil.offsetSecond(DateUtil.date(), expire);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL url = s3Client.generatePresignedUrl(config.getBucketName(), objectKey, expiration, HttpMethod.valueOf(method.toUpperCase()));
            apiResult.success(url.toString());
        } catch (com.aliyun.oss.ClientException e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "生成链接异常:" + e.getMessage());
        } finally {
            s3Client.shutdown();
        }
        return apiResult;
    }

    public ApiResult<JSONObject> getSts() {
        ApiResult<JSONObject> apiResult = new ApiResult<>();
        try {
            // 添加endpoint（直接使用STS endpoint，无需添加region ID）
            DefaultProfile.addEndpoint("", "Sts", "sts." + config.getRegion() + ".aliyuncs.com");
            // 构造default profile（参数留空，无需添加region ID）
            IClientProfile profile = DefaultProfile.getProfile("", config.getAccessKeyId(), config.getAccessKeySecret());
            // 用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);

            AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysProtocol(ProtocolType.HTTPS);
            request.setRoleArn(config.getRoleArn());
            request.setRoleSessionName(config.getRoleSessionName());
            // 若policy为空，则用户将获得该角色下所有权限
            request.setPolicy(null);
            // 设置凭证有效时间
            request.setDurationSeconds(config.getStsDurationSeconds());
            AssumeRoleResponse response = client.getAcsResponse(request);
            JSONObject resultJson = new JSONObject()
                    .set("accessKeyId", response.getCredentials().getAccessKeyId())
                    .set("accessKeySecret", response.getCredentials().getAccessKeySecret())
                    .set("securityToken", response.getCredentials().getSecurityToken())
                    .set("expiration", response.getCredentials().getExpiration())
                    .set("region", "oss-" + config.getRegion())
                    .set("prefix", config.getPrefix())
                    .set("domain", config.getDomain())
                    .set("secure", config.getSecure())
                    .set("bucketName", config.getBucketName());
            apiResult.success(resultJson);
        } catch (ClientException e) {
            log.error("aliyun oss get ste exception", e);
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "aliyun oss get ste exception:" + e.getMessage());
        }
        return apiResult;
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

package com.nb6868.onex.common.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.core.io.ReleasableInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static software.amazon.awssdk.utils.FunctionalUtils.invokeSafely;

/**
 * AWS S3 v2
 * see <a href="https://docs.aws.amazon.com/zh_cn/sdk-for-java/latest/developer-guide/examples-s3.html">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AwsS3Service extends AbstractOssService {

    private final S3Client s3Client;

    public AwsS3Service(OssPropsConfig config) {
        this.config = config;
        s3Client = initClient();
    }

    /**
     * 初始化client
     */
    private S3Client initClient() {
        try {
            return
                    S3Client.builder()
                            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKeyId(), config.getAccessKeySecret())))
                            .region(Region.AWS_GLOBAL)
                            .endpointOverride(URI.create(config.getEndPoint()))
                            .serviceConfiguration(S3Configuration.builder()
                                    .pathStyleAccessEnabled(false)
                                    .chunkedEncodingEnabled(true)
                                    .build())
                            .build();
        } catch (S3Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
    }

    @Override
    public ApiResult<InputStream> download(String objectKey) {
        ApiResult<InputStream> apiResult = new ApiResult<>();
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(objectKey)
                    .bucket(config.getBucketName())
                    .build();
            apiResult.success(s3Client.getObject(objectRequest));
        } catch (S3Exception e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件下载异常:" + e.getMessage());
        }
        return apiResult;
    }

    @Override
    public ApiResult<JSONObject> upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        ApiResult<JSONObject> apiResult = new ApiResult<>();
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(config.getBucketName())
                    .key(objectKey)
                    .build();
            // 默认使用的mimeType是Mimetype.MIMETYPE_OCTET_STREAM,做手动指定
            // RequestBody requestBody = RequestBody.fromInputStream(inputStream, inputStream.available());
            IoUtils.markStreamWithMaxReadLimit(inputStream);
            InputStream nonCloseable = ReleasableInputStream.wrap(inputStream).disableClose();
            RequestBody requestBody = RequestBody.fromContentProvider(() -> {
                if (nonCloseable.markSupported()) {
                    invokeSafely(nonCloseable::reset);
                }
                return nonCloseable;
            }, inputStream.available(), MapUtil.getStr(objectMetadataMap, "ContentType", Mimetype.MIMETYPE_OCTET_STREAM));
            PutObjectResponse putObjectResult = s3Client.putObject(putOb, requestBody);
            JSONObject resultJson = new JSONObject();
            resultJson.set("eTag", putObjectResult.eTag());
            resultJson.set("objectKey", objectKey);
            apiResult.success(resultJson);
        } catch (IOException | S3Exception e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件上传异常:" + e.getMessage());
        }
        return apiResult;
    }

    @Override
    public ApiResult<JSONObject> upload(String objectKey, File file) {
        ApiResult<JSONObject> apiResult = new ApiResult<>();
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(config.getBucketName())
                    .key(objectKey)
                    .build();
            PutObjectResponse putObjectResult = s3Client.putObject(putOb, RequestBody.fromFile(file));
            JSONObject resultJson = new JSONObject();
            resultJson.set("eTag", putObjectResult.eTag());
            apiResult.success(resultJson);
        } catch (S3Exception e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "文件上传异常:" + e.getMessage());
        }
        return apiResult;
    }

    /**
     * objectKey
     * expire 有效时间(秒)
     * see https://docs.aws.amazon.com/zh_cn/sdk-for-java/latest/developer-guide/examples-s3-presign.html
     */
    @Override
    public ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire) {
        ApiResult<String> apiResult = new ApiResult<>();
        try {
            S3Presigner presigner = S3Presigner.create();
            if ("get".equalsIgnoreCase(method)) {
                GetObjectRequest objectRequest = GetObjectRequest.builder()
                        .bucket(config.getBucketName())
                        // 设置过期时间
                        .responseExpires(DateUtil.toInstant(DateUtil.offsetSecond(new Date(), expire)))
                        .key(objectKey)
                        .build();
                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(expire))
                        .getObjectRequest(objectRequest)
                        .build();
                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
                // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
                apiResult.success(presignedRequest.url().toExternalForm());
            } else if ("put".equalsIgnoreCase(method)) {
                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(config.getBucketName())
                        // 设置过期时间
                        .expires(DateUtil.toInstant(DateUtil.offsetSecond(new Date(), expire)))
                        .key(objectKey)
                        .build();
                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(expire))
                        .putObjectRequest(objectRequest)
                        .build();
                PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
                // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
                apiResult.success(presignedRequest.url().toExternalForm());
            } else {
                apiResult.error(ApiResult.ERROR_CODE_PARAMS, "不支持的method方法:" + method);
            }
        } catch (S3Exception e) {
            apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "生成链接异常:" + e.getMessage());
        }
        return apiResult;
    }

    @Override
    public ApiResult<Boolean> isObjectKeyExisted(String bucketName, String objectKey) {
        // 给一个默认值，免得出错
        ApiResult<Boolean> apiResult = ApiResult.of(false);
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
            apiResult.setData(true);
        } catch (S3Exception e) {
            if (e instanceof NoSuchKeyException) {
                apiResult.setData(false);
            } else {
                apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, "doesObjectExist exception:" + e.getMessage());
            }
        }
        return apiResult;
    }
}

package com.nb6868.onex.common.oss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * AWS S3 v2
 * see <a href="https://docs.aws.amazon.com/zh_cn/sdk-for-java/latest/developer-guide/examples-s3.html">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AwsS3Service extends AbstractOssService {

    S3Client s3Client;

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
    public InputStream download(String objectKey) {
        S3Object s3Object = null;
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(objectKey)
                    .bucket(config.getBucketName())
                    .build();
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(objectRequest);
            return responseInputStream;
        } catch (S3Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
    }

    @Override
    public String upload(String prefix, InputStream inputStream, String fileName) {
        String prefixTotal = StrUtil.nullToEmpty(config.getPrefix());
        if (StrUtil.isNotEmpty(prefix)) {
            if (StrUtil.isNotEmpty(prefixTotal)) {
                prefixTotal += "/" + prefix;
            } else {
                prefixTotal = prefix;
            }
        }
        String objectKey = buildUploadPath(prefixTotal, fileName, config.getKeepFileName(), false);
        if (isObjectExisted(config.getBucketName(), objectKey)) {
            // 文件已存在,则需要对文件重命名
            objectKey = buildUploadPath(prefixTotal, fileName, config.getKeepFileName(), true);
        }
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(config.getBucketName())
                    .key(objectKey)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromInputStream(inputStream, inputStream.available()));
        } catch (IOException | S3Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }

        return config.getDomain() + objectKey;
    }

    @Override
    public String getPresignedUrl(String objectName, Long expire) {
        return null;
    }

    @Override
    public JSONObject getSts() {
        return null;
    }

    /**
     * 对象是否存在
     */
    private boolean isObjectExisted(String bucketName, String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
            return true;
        } catch (S3Exception e) {
            if (e instanceof NoSuchKeyException) {
                return false;
            } else {
                throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
            }
        }

    }
}

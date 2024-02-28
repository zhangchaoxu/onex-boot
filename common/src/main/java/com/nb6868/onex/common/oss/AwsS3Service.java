package com.nb6868.onex.common.oss;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
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
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(objectKey)
                    .bucket(config.getBucketName())
                    .build();
            return s3Client.getObject(objectRequest);
        } catch (S3Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
    }

    @Override
    public String upload(String objectKey, InputStream inputStream) {
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
    public String upload(String objectKey, File file) {
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(config.getBucketName())
                    .key(objectKey)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromFile(file));
        } catch (S3Exception e) {
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

    @Override
    public boolean isObjectKeyExisted(String bucketName, String objectKey) {
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

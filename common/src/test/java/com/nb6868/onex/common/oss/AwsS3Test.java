package com.nb6868.onex.common.oss;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.nio.file.Path;

@DisplayName("AwsS3Test")
@Slf4j
public class AwsS3Test {

    @Test
    @DisplayName("初始化bucket")
    void readBucketV2() {
        // ak:
        //sk:
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("", "")))
                .region(Region.AWS_GLOBAL)
                .endpointOverride(URI.create("https://oss-cn-hangzhou.aliyuncs.com"))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
        ListBucketsResponse lb = s3Client.listBuckets();
        for (software.amazon.awssdk.services.s3.model.Bucket b : lb.buckets()) {
            System.out.println(b.name());
        }
    }

    @Test
    @DisplayName("初始化bucket")
    void upload() {
        // ak:
        //sk:
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("", "")))
                .region(Region.AWS_GLOBAL)
                .endpointOverride(URI.create("https://oss-cn-hangzhou.aliyuncs.com"))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket("")
                .key("temp/" + IdUtil.fastSimpleUUID() + ".xlsx")
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromFile(Path.of("E:\\Downloads\\Y---20240226.xlsx")));
    }

    @Test
    @DisplayName("objectExisted")
    void objectExisted() throws S3Exception {
        S3Client s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("", "")))
                .region(Region.AWS_GLOBAL)
                .endpointOverride(URI.create("https://oss-cn-hangzhou.aliyuncs.com"))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build();
        HeadObjectRequest.builder()
                .bucket("")
                .key("temp/" + IdUtil.fastSimpleUUID() + ".xlsx")
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket("")
                .key("temp/9679f687eae04098b7199a2c7c44adbf.xlsx")
                .build());

        try {
            HeadObjectResponse headObjectResponse2 = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket("")
                    .key("temp/" + IdUtil.fastSimpleUUID() + ".xlsx")
                    .build());
        } catch (S3Exception s3Exception) {
            if (s3Exception instanceof NoSuchKeyException) {
                log.error("不存在");
            } else {
                log.error(s3Exception.getMessage());
            }
        }

        log.error("ss");
    }

}

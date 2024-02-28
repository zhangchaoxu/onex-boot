package com.nb6868.onex.common.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云存储
 * see <a href="https://help.aliyun.com/document_detail/32008.html">...</a>
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
    public String upload(String objectKey, InputStream inputStream) {
        try {
            s3Client.putObject(config.getBucketName(), objectKey, inputStream);
        } catch (OSSException | com.aliyun.oss.ClientException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            s3Client.shutdown();
        }

        return config.getDomain() + objectKey;
    }

    @Override
    public InputStream download(String objectKey) {
        OSSObject ossObject = null;
        try {
            ossObject = s3Client.getObject(config.getBucketName(), objectKey);
            return ossObject.getObjectContent();
        } catch (OSSException | com.aliyun.oss.ClientException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
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
    }


    @Override
    public String getPresignedUrl(String objectName, Long expire) {
        try {
            // 设置URL过期时间。
            Date expiration = DateUtil.offsetSecond(DateUtil.date(), expire.intValue());
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL url = s3Client.generatePresignedUrl(config.getBucketName(), objectName, expiration);
            return url.toString();
        } catch (com.aliyun.oss.ClientException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            s3Client.shutdown();
        }
    }

    @Override
    public JSONObject getSts() {
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
            return new JSONObject()
                    .set("accessKeyId", response.getCredentials().getAccessKeyId())
                    .set("accessKeySecret", response.getCredentials().getAccessKeySecret())
                    .set("securityToken", response.getCredentials().getSecurityToken())
                    .set("expiration", response.getCredentials().getExpiration())
                    .set("region", "oss-" + config.getRegion())
                    .set("prefix", config.getPrefix())
                    .set("domain", config.getDomain())
                    .set("secure", config.getSecure())
                    .set("bucketName", config.getBucketName());
        } catch (ClientException e) {
            throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, e);
        }
    }

    @Override
    public boolean isObjectKeyExisted(String bucketName, String objectKey) {
        return s3Client.doesObjectExist(bucketName, objectKey);
    }
}

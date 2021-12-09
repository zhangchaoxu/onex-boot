package com.nb6868.onex.common.oss;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 华为云OBS存储
 * see {https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_0105.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class HuaweiCloudOssService extends AbstractOssService {

    public HuaweiCloudOssService(OssProps.Config config) {
        this.config = config;
    }

    @Override
    public InputStream download(String objectKey) {
        ObsClient ossClient = null;
        ObsObject ossObject = null;
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
    public String upload(MultipartFile file) {
        return upload(null, file);
    }

    @Override
    public String upload(File file) {
        return upload(null, file);
    }

    @Override
    public String upload(String prefix, MultipartFile file) {
        String prefixTotal = StrUtil.isNotEmpty(config.getPrefix()) ? config.getPrefix() : "";
        if (StrUtil.isNotEmpty(prefix)) {
            if (StrUtil.isNotEmpty(prefixTotal)) {
                prefixTotal += "/" + prefix;
            } else {
                prefixTotal = prefix;
            }
        }
        String objectKey = buildUploadPath(prefixTotal, file.getOriginalFilename(), config.getKeepFileName(), false);
        ObsClient ossClient = null;
        try {
            ossClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
            if (ossClient.doesObjectExist(config.getBucketName(), objectKey)) {
                // 文件已存在,则需要对文件重命名
                objectKey = buildUploadPath(prefixTotal, file.getOriginalFilename(), config.getKeepFileName(), true);
            }
            ossClient.putObject(config.getBucketName(), objectKey, file.getInputStream());
        } catch (ObsException | IOException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            // 关闭ObsClient实例，如果是全局ObsClient实例，可以不在每个方法调用完成后关闭
            // ObsClient在调用ObsClient.close方法关闭后不能再次使用
            if (ossClient != null) {
                try {
                    ossClient.close();
                } catch (IOException e) {
                    log.error("huaweicloud obs close error", e);
                }
            }
        }

        return config.getDomain() + objectKey;
    }

    @Override
    public String upload(String prefix, File file) {
        String prefixTotal = StrUtil.isNotEmpty(config.getPrefix()) ? config.getPrefix() : "";
        if (StrUtil.isNotEmpty(prefix)) {
            if (StrUtil.isNotEmpty(prefixTotal)) {
                prefixTotal += "/" + prefix;
            } else {
                prefixTotal = prefix;
            }
        }
        String objectKey = buildUploadPath(prefixTotal, FileNameUtil.getName(file), config.getKeepFileName(), false);
        ObsClient ossClient = null;
        try {
            ossClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
            if (ossClient.doesObjectExist(config.getBucketName(), objectKey)) {
                // 文件已存在,则需要对文件重命名
                objectKey = buildUploadPath(prefixTotal, FileNameUtil.getName(file), config.getKeepFileName(), true);
            }
            ossClient.putObject(config.getBucketName(), objectKey, file);
        } catch (ObsException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            // 关闭ObsClient实例，如果是全局ObsClient实例，可以不在每个方法调用完成后关闭
            // ObsClient在调用ObsClient.close方法关闭后不能再次使用
            if (ossClient != null) {
                try {
                    ossClient.close();
                } catch (IOException e) {
                    log.error("huaweicloud obs close error", e);
                }
            }
        }

        return config.getDomain() + objectKey;
    }

    @Override
    public String upload(InputStream inputStream, String fileName) {
        return this.upload(null, inputStream, fileName);
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
        ObsClient ossClient = null;
        try {
            ossClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeySecret(), config.getEndPoint());
            if (ossClient.doesObjectExist(config.getBucketName(), objectKey)) {
                // 文件已存在,则需要对文件重命名
                objectKey = buildUploadPath(prefixTotal, fileName, config.getKeepFileName(), true);
            }
            ossClient.putObject(config.getBucketName(), objectKey, inputStream);
        } catch (ObsException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        } finally {
            // 关闭ObsClient实例，如果是全局ObsClient实例，可以不在每个方法调用完成后关闭
            // ObsClient在调用ObsClient.close方法关闭后不能再次使用
            if (ossClient != null) {
                try {
                    ossClient.close();
                } catch (IOException e) {
                    log.error("huaweicloud obs close error", e);
                }
            }
        }

        return config.getDomain() + objectKey;
    }

    @Override
    public String generatePresignedUrl(String objectName, long expiration) {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "华为云存储暂不支持生成url模式");
    }

    @Override
    public Dict getSts() {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "华为云存储暂不支持sts模式");
    }
}

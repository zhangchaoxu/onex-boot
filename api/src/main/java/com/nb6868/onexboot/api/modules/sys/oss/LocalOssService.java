package com.nb6868.onexboot.api.modules.sys.oss;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Kv;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 本地上传
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LocalOssService extends AbstractOssService {

    public LocalOssService(OssProp config) {
        this.config = config;
    }

    @Override
    public String upload(MultipartFile file) {
        String objectKey = buildUploadPath(config.getPrefix(), FilenameUtils.getExtension(file.getOriginalFilename()));
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        try {
            FileUtils.copyToFile(file.getInputStream(), localFile);
        } catch (IOException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
        return config.getDomain() + objectKey;
    }

    @Override
    public String generatePresignedUrl(String objectName, long expiration) {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持生成url模式");
    }

    @Override
    public Kv getSts() {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持sts模式");
    }
}

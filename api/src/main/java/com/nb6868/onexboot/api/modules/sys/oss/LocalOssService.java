package com.nb6868.onexboot.api.modules.sys.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
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
        String objectKey = buildUploadPath(config.getPrefix(), file.getOriginalFilename(), config.getKeepFileName(), false);
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        if (localFile.exists()) {
            // 文件已存在,则需要对文件重命名
            objectKey = buildUploadPath(config.getPrefix(), file.getOriginalFilename(), config.getKeepFileName(), true);
        }
        BufferedOutputStream out = FileUtil.getOutputStream(localFile);
        try {
            IoUtil.copy(file.getInputStream(), out);
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
    public Dict getSts() {
        throw new OnexException(ErrorCode.OSS_CONFIG_ERROR, "本地存储不支持sts模式");
    }
}

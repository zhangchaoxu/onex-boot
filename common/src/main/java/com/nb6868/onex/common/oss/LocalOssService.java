package com.nb6868.onex.common.oss;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地上传
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LocalOssService extends AbstractOssService {

    public LocalOssService(OssProps.Config config) {
        this.config = config;
    }

    @Override
    public InputStream download(String objectKey) {
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        if (localFile.exists()) {
            return IoUtil.toStream(localFile);
        } else {
            return null;
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
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        if (localFile.exists()) {
            // 文件已存在,则需要对文件重命名
            objectKey = buildUploadPath(prefixTotal, file.getOriginalFilename(), config.getKeepFileName(), true);
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
        File localFile = new File(config.getLocalPath() + File.separator + objectKey);
        if (localFile.exists()) {
            // 文件已存在,则需要对文件重命名
            objectKey = buildUploadPath(prefixTotal, FileNameUtil.getName(file), config.getKeepFileName(), true);
        }
        FileUtil.copy(file, localFile, true);
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

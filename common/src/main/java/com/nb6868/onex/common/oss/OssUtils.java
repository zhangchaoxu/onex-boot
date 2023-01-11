package com.nb6868.onex.common.oss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OssUtils {

    @Value("${onex.oss.file-storage-path}")
    private String ossFileStoragePath;
    @Value("${onex.oss.file-request-path}")
    private String ossFileRequestPath;

    public String getOssPath() {
        return ossFileRequestPath.replaceAll("\\*", "");
    }

    public String getOssFileStoragePath() {
        return ossFileStoragePath;
    }

}

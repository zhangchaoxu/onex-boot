package com.nb6868.onex.common.oss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OssUtils {

    @Value("${onex.api-path}")
    private String apiPath;
    @Value("${onex.oss.file-storage-path}")
    private String ossFileStoragePath;
    @Value("${onex.oss.file-request-path}")
    private String ossFileRequestPath;

    public String getOssPath() {
        return apiPath + ossFileRequestPath.replace("**", "");
    }

    public String getOssFileStoragePath() {
        return ossFileStoragePath;
    }

    public String getApiPath() {
        return apiPath;
    }

}

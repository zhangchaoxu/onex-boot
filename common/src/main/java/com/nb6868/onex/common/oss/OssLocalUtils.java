package com.nb6868.onex.common.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * OSS 工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class OssLocalUtils {

    // 文件存储路径
    private static String ossFileStoragePath;
    private static String ossFileRequestPath;

    @Value("${onex.oss.file-storage-path}")
    public void setOssFileStoragePath(String path) {
        ossFileStoragePath = path;
    }

    @Value("${onex.oss.file-request-path}")
    public void setOssFileRequestPath(String path) {
        ossFileRequestPath = path;
    }

    public static String getOssRequestPrefix() {
        return ossFileRequestPath.replaceAll("\\*", "");
    }

    public static String getOssFileRequestPath() {
        return ossFileRequestPath;
    }

    public static String getOssFileStoragePath() {
        return ossFileStoragePath;
    }

    /**
     * 获得存储绝对路径,以斜杠结尾
     */
    public static String getOssFileStorageAbsolutePath() {
        // 若直接使用./xxx会在api.jar!/BOOT-INF/classes!/下创建文件夹,而不是在jar包下
        // System.getProperty("user.dir")可以获得jar路径，不带斜杠
        return FileUtil.isAbsolutePath(ossFileStoragePath) ? ossFileStoragePath : (System.getProperty("user.dir") + StrUtil.removePrefix(ossFileStoragePath, "."));
    }

}

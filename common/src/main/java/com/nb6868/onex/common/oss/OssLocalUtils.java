package com.nb6868.onex.common.oss;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * OSS 工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class OssLocalUtils {

    public final static String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    public final static String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
    public final static String FILENAME_XLS_FMT = "attachment;filename={}.xls";
    public final static String FILENAME_XLSX_FMT = "attachment;filename={}.xlsx";
    public final static String FILENAME_XLS_SUFFIX = ".xls";
    public final static String FILENAME_XLSX_SUFFIX = ".xlsx";

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

    /**
     * 格式化文件名
     * 补充yyyyMMddHHmmssSSS,默认同一毫秒内没有同一操作
     */
    public static String fmtXlsxFileName(String folderName, String fileName) {
        if (StrUtil.isBlank(folderName)) {
            return StrUtil.format("{}-{}{}", fileName, DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT), FILENAME_XLSX_SUFFIX);
        } else {
            return StrUtil.format("{}{}-{}{}", StrUtil.endWith(folderName, "/") ? folderName : folderName + "/", fileName, DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT), FILENAME_XLSX_SUFFIX);
        }
    }

}

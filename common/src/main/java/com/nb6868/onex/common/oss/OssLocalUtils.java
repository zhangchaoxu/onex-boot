package com.nb6868.onex.common.oss;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
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
@Slf4j
public class OssLocalUtils {

    public final static String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    public final static String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
    public final static String FILENAME_XLS_FMT = "attachment;filename={}.xls";
    public final static String FILENAME_XLSX_FMT = "attachment;filename={}.xlsx";
    public final static String FILENAME_XLS_SUFFIX = ".xls";
    public final static String FILENAME_XLSX_SUFFIX = ".xlsx";

    // 文件存储路径
    @Getter
    private static String ossFileStoragePath;
    @Getter
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

    /**
     * 获得存储绝对路径,以斜杠结尾
     */
    public static String getOssFileStorageAbsolutePath() {
        // 若直接使用./xxx会在api.jar!/BOOT-INF/classes!/下创建文件夹,而不是在jar包下
        // System.getProperty("user.dir")可以获得jar路径，不带斜杠,但是会出现路径问题
        // new ApplicationHome().getSource().getParent()
        // https://mp.weixin.qq.com/s?__biz=Mzk0OTUxNjY2Ng==&mid=2247487629&idx=1&sn=203cfcc479f30823e6851e006bc60960
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

    /**
     * 设置使用SXSSFWorkbook对象导出excel报表时，TempFile使用的临时目录，代替{java.io.tmpdir}
     * excel导出的时候会在/tmp/poifiles下创建目录，而centos会定期清空该目录，导出出错
     * <a href="https://www.cnblogs.com/wenboonly/p/14922090.html">SXSSFWorkbook POI 临时文件夹“poifiles”问题处理</a>
     */
    @PostConstruct
    public void setExcelSXSSFWorkbookTmpPath() {
        String excelSXSSFWorkbookTmpPath = getOssFileStorageAbsolutePath() + "poifiles";
        File dir = FileUtil.mkdir(excelSXSSFWorkbookTmpPath);
        TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(dir));
        log.info("setExcelSXSSFWorkbookTmpPath={}", excelSXSSFWorkbookTmpPath);
    }

}

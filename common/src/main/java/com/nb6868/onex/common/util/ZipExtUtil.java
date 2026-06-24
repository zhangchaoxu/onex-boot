package com.nb6868.onex.common.util;

import cn.hutool.core.compress.ZipReader;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Zip工具类
 * hutool的ziputil有zip炸弹的检查(100倍)，但实际会有超过的情况，所以重新实现一个
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ZipExtUtil extends ZipUtil {

    /**
     * 解析
     * @param zipFilePath
     * @param outFileDir
     * @return
     * @throws UtilException
     */
    public static File unzip(String zipFilePath, String outFileDir) throws UtilException {
        return unzip(toZipFile(FileUtil.file(zipFilePath), CharsetUtil.defaultCharset()), FileUtil.mkdir(outFileDir), -1);
    }

    public static File unzip(ZipFile zipFile, File outFile, long limit) throws IORuntimeException {
        if (outFile.exists() && outFile.isFile()) {
            throw new IllegalArgumentException(
                    StrUtil.format("Target path [{}] exist!", outFile.getAbsolutePath()));
        }

        // pr#726@Gitee
        if (limit > 0) {
            final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            long zipFileSize = 0L;
            while (zipEntries.hasMoreElements()) {
                final ZipEntry zipEntry = zipEntries.nextElement();
                zipFileSize += zipEntry.getSize();
                if (zipFileSize > limit) {
                    throw new IllegalArgumentException("The file size exceeds the limit");
                }
            }
        }

        try (final ZipReader reader = new ZipReader(zipFile)) {
            // 关键在这里，设置最大倍数差
            reader.setMaxSizeDiff(-1);
            reader.readTo(outFile);
        }
        return outFile;
    }

}
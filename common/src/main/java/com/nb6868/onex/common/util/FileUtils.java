package com.nb6868.onex.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;

import java.awt.*;
import java.io.File;

/**
 * 文件工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class FileUtils {

    /**
     * 将文件对象转换为Base64的Data URI形式，格式为：data:[mineType];base64,[data]
     *
     * @param filePath 文件路径
     * @return Base64的字符串表现形式
     */
    public static String toBase64DataUri(String filePath) {
        if (FileUtil.exist(filePath)) {
            return toBase64DataUri(FileUtil.file(filePath));
        } else {
            return null;
        }
    }

    /**
     * 将文件对象转换为Base64的Data URI形式，格式为：data:[mineType];base64,[data]
     *
     * @param file 文件对象
     * @return Base64的字符串表现形式
     */
    public static String toBase64DataUri(File file) {
        return URLUtil.getDataUriBase64(FileUtil.getMimeType(file.getName()), toBase64(file));
    }

    /**
     * 将文件对象转换为Base64形式
     *
     * @param file 文件对象
     * @return Base64的字符串表现形式
     */
    public static String toBase64(File file) {
        return Base64.encode(file);
    }

    /**
     * 将文件对象转换为Base64形式
     *
     * @param filePath 文件路径
     * @return Base64的字符串表现形式
     */
    public static String toBase64(String filePath) {
        if (FileUtil.exist(filePath)) {
            return toBase64(FileUtil.file(filePath));
        } else {
            return null;
        }
    }

}

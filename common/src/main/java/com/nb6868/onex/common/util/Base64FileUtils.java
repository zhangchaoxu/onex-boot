package com.nb6868.onex.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 文件base64工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class Base64FileUtils {

    private static final Map<String, String> FILE_TYPE_MAP;

    static {
        FILE_TYPE_MAP = new ConcurrentSkipListMap<>((s1, s2) -> {
            int len1 = s1.length();
            int len2 = s2.length();
            if (len1 == len2) {
                return s1.compareTo(s2);
            } else {
                return len2 - len1;
            }
        });

        FILE_TYPE_MAP.put("image/jpeg", "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("image/pjpeg", "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("image/png", "png"); // PNG (png)
        FILE_TYPE_MAP.put("image/x-png", "png"); // PNG (png)
        FILE_TYPE_MAP.put("application/vnd.ms-excel", "xls"); // EXCEL/CSV
        FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"); // EXCEL
        FILE_TYPE_MAP.put("application/msword", "doc"); // DOC
        FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"); // DOC
        FILE_TYPE_MAP.put("application/vnd.ms-powerpoint", "ppt"); // ppt
        FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"); // pptx
        FILE_TYPE_MAP.put("application/x-zip-compressed", "zip"); // ZIP
        FILE_TYPE_MAP.put("text/plain", "txt"); // TEXT

    }

    /**
     * 获得文件后缀名
     *
     * @param base64 文件base64
     * @return 获得文件contentType
     */
    public static String getContentType(String base64) {
        if (base64.split(",").length == 2) {
            String content = base64.split(",")[0];
            content = StrUtil.removePrefix(content, "data:");
            content = StrUtil.removePrefix(content, ";base64");
            return content;
        } else {
            return "";
        }
    }

    /**
     * 根据文件contentType获得文件类型
     * 这个功能谨慎使用，因为会判断失败，application/vnd.ms-excel可能是xls，也可能是csv
     *
     * @param contentType 文件contentType
     * @return 文件类型，未找到为null
     */
    public static String getFileTypeByContentType(String contentType) {
        for (Map.Entry<String, String> fileTypeEntry : FILE_TYPE_MAP.entrySet()) {
            if (StrUtil.startWithIgnoreCase(contentType, fileTypeEntry.getKey())) {
                return fileTypeEntry.getValue();
            }
        }
        return null;
    }

}

package com.nb6868.onex.common.oss;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 存储服务(阿里云、本地)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractOssService {

    public OssPropsConfig config;

    /**
     * 文件路径
     *
     * @param pathPrefix      路径前缀
     * @param fileName        文件名
     * @param keepFileName    是否保留原文件名
     * @param appendTimestamp 文件名追加时间戳
     * @return 返回上传路径
     */
    public String buildUploadPath(String pathPrefix, String fileName, boolean keepFileName, boolean appendTimestamp) {
        // 路径：文件路径,按日分割
        String path = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN) + "/";
        if (StrUtil.isNotBlank(pathPrefix)) {
            path = pathPrefix + "/" + path;
        }
        // 文件
        String newFileName;
        if (keepFileName) {
            // 保留原文件名
            if (appendTimestamp) {
                String fileExtensionName = FileNameUtil.extName(fileName);
                if (StrUtil.isNotBlank(fileExtensionName)) {
                    newFileName = FileNameUtil.mainName(fileName) + "-" + DateUtil.format(DateUtil.date(), "HHmmssSSS") + "." + fileExtensionName;
                } else {
                    newFileName = FileNameUtil.getName(fileName) + "-" + DateUtil.format(DateUtil.date(), "HHmmssSSS");
                }
            } else {
                newFileName = fileName;
            }
        } else {
            // 生成uuid
            String uuid = IdUtil.simpleUUID();
            String fileExtensionName = FileNameUtil.extName(fileName);
            if (StrUtil.isNotBlank(fileExtensionName)) {
                if (appendTimestamp) {
                    newFileName = uuid + "-" + DateUtil.format(DateUtil.date(), "HHmmssSSS") + "." + fileExtensionName;
                } else {
                    newFileName = uuid + "." + fileExtensionName;
                }
            } else {
                newFileName = uuid;
            }
        }
        return path + newFileName;
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 返回http地址
     */
    public String upload(MultipartFile file) {
        return upload(null, file);
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 返回http地址
     */
    public String upload(File file) {
        return upload(null, file);
    }

    /**
     * 文件上传
     *
     * @param inputStream 文件流
     * @param fileName    文件名
     * @return 返回http地址
     */
    public String upload(InputStream inputStream, String fileName) {
        return upload(null, inputStream, fileName);
    }

    /**
     * 文件上传
     *
     * @param prefix      文件路径前缀
     * @param inputStream 文件流
     * @param fileName    文件名
     * @return 返回http地址
     */
    public abstract String upload(String prefix, InputStream inputStream, String fileName);

    /**
     * 文件上传
     *
     * @param prefix 文件路径前缀
     * @param file   文件
     * @return 返回http地址
     */
    public String upload(String prefix, MultipartFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
        return upload(prefix, inputStream, file.getOriginalFilename());
    }

    /**
     * 文件上传
     *
     * @param prefix 文件路径前缀
     * @param file   文件
     * @return 返回http地址
     */
    public String upload(String prefix, File file) {
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        return upload(prefix, inputStream, FileNameUtil.getName(file));
    }

    /**
     * base64 上传文件
     *
     * @param prefix
     * @param base64
     * @return
     */
    public String uploadBase64(String prefix, String base64, String fileName) {
        InputStream inputStream;
        try {
            if (base64.split(",").length > 1) {
                base64 = base64.split(",")[1];
            }
            inputStream = IoUtil.toStream(Base64Decoder.decode(base64));
        } catch (Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
        return upload(prefix, inputStream, fileName);
    }

    /**
     * base64 上传文件
     * @param base64
     * @param fileName
     * @return
     */
    public String uploadBase64(String base64, String fileName) {
        return uploadBase64(null, base64, fileName);
    }

    /**
     * 文件下载
     *
     * @param objectKey 文件名
     */
    public abstract InputStream download(String objectKey);

    /**
     * 生成访问时间
     */
    public abstract String getPresignedUrl(String objectName, Long expiration);

    /**
     * 获得sts
     */
    public abstract JSONObject getSts();

}

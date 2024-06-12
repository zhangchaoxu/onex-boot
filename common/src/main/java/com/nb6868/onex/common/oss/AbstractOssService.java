package com.nb6868.onex.common.oss;

import cn.hutool.core.codec.Base64;
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
 * 存储服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractOssService {

    public OssPropsConfig config;

    /**
     * 文件路径前缀
     *
     * @param prefixGlobal      路径前缀全局
     * @param prefixCustom      路径前缀自定义
     * @return 返回上传路径
     */
    public String buildPathPrefix(String prefixGlobal, String prefixCustom) {
        String prefix = StrUtil.emptyIfNull(prefixGlobal) + (StrUtil.isNotBlank(prefixCustom) ? ("/" + prefixCustom) : "");
        return StrUtil.removePrefix(prefix, "/");
    }

    public String buildObjectKey(String bucketName, String pathPrefix, String pathPolicy, String fileName, boolean keepFileName) {
        String path = buildPath(pathPrefix, pathPolicy);
        String newFileName = buildFileName(bucketName, path, fileName, keepFileName);
        return path + newFileName;
    }

    /**
     * 构建path
     */
    public String buildPath(String pathPrefix, String pathPolicy) {
        // 先不上自定义的prefix与斜杠
        String path = StrUtil.nullToEmpty(pathPrefix);
        if ("uuid".equalsIgnoreCase(pathPolicy)) {
            path += ("/" + IdUtil.fastSimpleUUID());
        } else if ("day".equalsIgnoreCase(pathPolicy)) {
            path += ("/" + DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN));
        } else if ("month".equalsIgnoreCase(pathPolicy)) {
            path += ("/" + DateUtil.format(DateUtil.date(), DatePattern.SIMPLE_MONTH_PATTERN));
        } else if ("year".equalsIgnoreCase(pathPolicy)) {
            path += ("/" + DateUtil.format(DateUtil.date(), DatePattern.NORM_YEAR_PATTERN));
        }
        // 补上斜杠
        if (StrUtil.isNotBlank(path)) {
            path += "/";
        }
        return StrUtil.removePrefix(path, "/");
    }

    /**
     * 创建文件名
     */
    public String buildFileName(String bucketName, String path, String fileName, boolean keepFileName) {
        // 文件
        String newFileName;
        if (keepFileName) {
            String fileExtName = FileNameUtil.extName(fileName);
            String fileMainName = FileNameUtil.mainName(fileName);
            // 去除urlencode不支持字符,去除容易出问题的逗号
            String fileMainNameNoSpecChar = StrUtil.removeAll(fileMainName, ' ', '+', '=', '&', '#', '/', '?', '%', '*', ',', '，');
            // 新的文件名
            newFileName = fileMainNameNoSpecChar + (StrUtil.isNotBlank(fileExtName) ? ("." + fileExtName) : "");
            if (isObjectKeyExisted(bucketName, path + newFileName)) {
                // 若objectKey已存在,补一个后缀,默认补上后缀后不会再重复
                newFileName = fileMainNameNoSpecChar + "-" + DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + (StrUtil.isNotBlank(fileExtName) ? ("." + fileExtName) : "");
            }
        } else {
            // 文件扩展名
            String fileExtName = FileNameUtil.extName(fileName);
            // 生成{uuid}.{extName}
            newFileName = IdUtil.simpleUUID() + (StrUtil.isNotBlank(fileExtName) ? ("." + fileExtName) : "");
        }
        return newFileName;
    }

    /**
     * 文件上传
     *
     * @param objectKey    路径前缀+文件名
     * @param inputStream 文件流
     * @return 返回objectKey
     */
    public abstract String upload(String objectKey, InputStream inputStream);

    public String upload(String objectKey, MultipartFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
        return upload(objectKey, inputStream);
    }

    /**
     * 文件上传
     *
     * @param objectKey 文件路径前缀
     * @param file   文件
     * @return 返回http地址
     */
    public String upload(String objectKey, File file) {
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        return upload(objectKey, inputStream);
    }

    /**
     * base64 上传文件
     *
     * @param objectKey 前缀
     * @param base64 文件base64
     * @return 上传结果
     */
    public String uploadBase64(String objectKey, String base64) {
        InputStream inputStream;
        try {
            if (base64.split(",").length > 1) {
                base64 = base64.split(",")[1];
            }
            inputStream = IoUtil.toStream(Base64.decode(base64));
        } catch (Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e);
        }
        return upload(objectKey, inputStream);
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

    /**
     * object key是否存在
     */
    public abstract boolean isObjectKeyExisted(String bucketName, String objectKey);

}

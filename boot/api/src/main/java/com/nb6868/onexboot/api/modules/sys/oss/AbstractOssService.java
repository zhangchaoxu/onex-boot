package com.nb6868.onexboot.api.modules.sys.oss;

import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 云存储(阿里云、本地)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractOssService {

    /**
     * 云存储配置信息
     */
    OssProp config;

    /**
     * 文件路径
     *
     * @param pathPrefix 路径前缀
     * @param fileExtensionName 文件扩展名
     * @return 返回上传路径
     */
    public String buildUploadPath(String pathPrefix, String fileExtensionName) {
        // 生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 文件路径,按日分割
        String path = DateUtils.format(DateUtils.now(), "yyyyMMdd") + "/" + uuid;

        if (StringUtils.isNotBlank(pathPrefix)) {
            path = pathPrefix + "/" + path;
        }
        if (StringUtils.isNotBlank(fileExtensionName)) {
            path = path + "." + fileExtensionName;
        }

        return path;
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 返回http地址
     */
    public abstract String upload(MultipartFile file);

    /**
     * 生成访问时间
     */
    public abstract String generatePresignedUrl(String objectName, long expiration);

    /**
     * 获得sts
     */
    public abstract Kv getSts();

}

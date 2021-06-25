package com.nb6868.onexboot.api.modules.sys.oss;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onexboot.common.pojo.Kv;
import org.springframework.web.multipart.MultipartFile;

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
     * @param fileName 文件扩展名
     * @param keepFileName 是否保留原文件名
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

package com.nb6868.onex.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MultipartFile转换工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
@Slf4j
public class MultipartFileUtils {

    /**
     * 文件上传路径
     */
    private static String uploadPath;

    @PostConstruct
    public void init() {
        // 若定义了upload-path,则获取上传路径
        String path = SpringUtil.getProperty("onex.upload-path");
        if (StrUtil.isBlank(path)) {
            path = "./onex-upload";
        }
        uploadPath = path;
    }

    /**
     * 获得上传路径
     */
    public static String getUploadPath() {
        return uploadPath;
    }

    /**
     * MultipartFile保存为本地File
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        // 保存文件
        String fileName = uploadPath + File.separator + multipartFile.getOriginalFilename();
        File localFile;
        if (FileUtil.exist(fileName)) {
            // 文件已存在
            String fileExtensionName = FileNameUtil.extName(multipartFile.getOriginalFilename());
            StringBuilder newFileName = new StringBuilder()
                    .append(FileNameUtil.mainName(multipartFile.getOriginalFilename()))
                    .append("-")
                    .append(DateUtil.format(DateUtil.date(), "HHmmssSSS"));
            if (StrUtil.isNotBlank(fileExtensionName)) {
                newFileName.append(".").append(fileExtensionName);
            }
            localFile = FileUtil.touch(uploadPath + File.separator + newFileName);
        } else {
            localFile = FileUtil.touch(fileName);
        }
        try {
            IoUtil.copy(multipartFile.getInputStream(), FileUtil.getOutputStream(localFile));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return localFile;
    }

    /**
     * 本地File转MultipartFile
     * 也可使用MockMultipartFile
     */
    public static MultipartFile fileToMultipartFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        FileItem fileItem = new DiskFileItemFactory(16, null)
                .createItem("textField", "text/plain", true, file.getName());
        try {
            IoUtil.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new CommonsMultipartFile(fileItem);
    }

    /**
     * base64转文件
     */
    public static File base64ToFile(String base64) {
        String filePath = uploadPath + File.separator + IdUtil.simpleUUID();
        return base64ToFile(base64, filePath);
    }

    /**
     * base64转文件,指定文件
     */
    public static File base64ToFile(String base64, String filePath) {
        // data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,{base64_string}
        try {
            return Base64.decodeToFile(base64.contains(",") ? base64.split(",")[1] : base64, new File(filePath));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * base64转MultipartFile
     */
    public static MultipartFile base64ToMultipartFile(String base64) {
        return fileToMultipartFile(base64ToFile(base64));
    }

    /**
     * base64转MultipartFile,指定路径
     */
    public static MultipartFile base64ToMultipartFile(String base64, String filePath) {
        return fileToMultipartFile(base64ToFile(base64, filePath));
    }

}

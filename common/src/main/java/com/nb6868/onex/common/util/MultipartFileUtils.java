package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

/**
 * MultipartFile转换工具
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Component
public class MultipartFileUtils {

    /**
     * value注解不能直接给静态变量赋值
     */
    private static String uploadPath;

    @Value("${server.tomcat.basedir}")
    public void setUploadPath(String uploadPath) {
        MultipartFileUtils.uploadPath = uploadPath;
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
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem fileItem = factory.createItem("textField", "text/plain", true, file.getName());
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
        String[] baseStrs = base64.split(",");
        String fileName = uploadPath + File.separator + IdUtil.simpleUUID();

        try {
            Files.write(Paths.get(fileName), Base64.getDecoder().decode(baseStrs[1]), StandardOpenOption.CREATE);
        } catch (IOException e) {
            return null;
        }
        return new File(fileName);
    }

    public static MultipartFile base64ToMultipartFile(String base64) {
        return fileToMultipartFile(base64ToFile(base64));
    }

}
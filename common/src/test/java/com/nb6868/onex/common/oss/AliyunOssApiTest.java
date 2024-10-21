package com.nb6868.onex.common.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.dialect.Props;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

@DisplayName("阿里云OSS接口测试")
@Slf4j
public class AliyunOssApiTest {

    @Test
    @DisplayName("文件上传")
    void uploadFile() {
        //AliyunOssApi.putObject("");
        log.error(FileUtil.getMimeType("/2/1"));
        log.error(FileUtil.getMimeType("1.png"));
        log.error(FileUtil.getMimeType("1.jpeg"));
        log.error(FileUtil.getMimeType("1.txt"));
        log.error(FileUtil.getMimeType("1.md"));
        String path = "D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".txt";
        FileUtil.writeUtf8String("sss", path);

        FileUtil.file("D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".txt");
        Props props = new Props("confidential/oss.properties");
        ApiResult<JSONObject> result = AliyunOssApi.putObject(props.getStr("accessKeyId"), props.getStr("accessKeySecret"), props.getStr("endPoint"), props.getStr("region"), props.getStr("bucketName"), "test/换行 " + IdUtil.fastSimpleUUID() + ".txt", null, FileUtil.getInputStream(path));
        log.error(result.toString());
    }

    @Test
    @DisplayName("文件下载")
    void downloadFile() {
        // FileUtil.file("D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".pdf");
        Props props = new Props("confidential/oss.properties");
        ApiResult<InputStream> result = AliyunOssApi.getObject(props.getStr("accessKeyId"), props.getStr("accessKeySecret"), props.getStr("endPoint"), props.getStr("region"), props.getStr("bucketName"), "20240226/《网络销售特殊食品安全合规指南》.pdf", null);
        log.error(result.isSuccess() + "");
        if (result.isSuccess()) {
            FileUtil.writeFromStream(result.getData(), "D:\\WorkspacesData\\dc\\" + IdUtil.fastSimpleUUID() +".pdf");
        }
    }

    @Test
    @DisplayName("生成预签名链接")
    void getPreSignUrl() {
        Props props = new Props("confidential/oss.properties");

        ApiResult<String> result = AliyunOssApi.getPreSignedUrl(props.getStr("accessKeyId"), props.getStr("accessKeySecret"), props.getStr("endPoint"), props.getStr("region"), props.getStr("bucketName"), "test/换行 ff2ad93956484509912dbe440f324551.txt", null, "put", 10 * 60);
        log.error(result.toString());
        log.error(result.getData());
    }
}

package com.nb6868.onex.common.oss;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("阿里云OSS接口测试")
@Slf4j
public class AliyunOssApiTest {

    @Test
    @DisplayName("签名V4")
    void signV4() {
        //AliyunOssApi.putObject("");
        log.error(FileUtil.getMimeType("/2/1"));
        log.error(FileUtil.getMimeType("1.png"));
        log.error(FileUtil.getMimeType("1.jpeg"));
        log.error(FileUtil.getMimeType("1.txt"));
        log.error(FileUtil.getMimeType("1.md"));
    }
}

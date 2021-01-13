package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.common.util.QrCodeUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

/**
 * 二维码测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QrCodeTest {

    @Test
    public void generateQrcode() throws IOException {
        File file = QrCodeUtils.from("Hello World").withSize(600, 800).file("qrtest");
        FileUtils.copyFileToDirectory(file, new File("C:\\"));
    }

}

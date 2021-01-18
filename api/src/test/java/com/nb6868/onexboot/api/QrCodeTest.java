package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.common.util.QrCodeUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 二维码测试
 *
 * @author Charles zhangchaoxu@gmail.com
 */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QrCodeTest {

    @Test
    public void generateQrcode() {
        QrCodeUtils.text("ni收到试试试试").withSize(600, 600).writeToFile(new File("C:\\1234.png"));
        System.out.println("解码:" + QrCodeUtils.decodeQrcode(new File("C:\\1234.png")));
        System.out.println("解码:" + StandardCharsets.UTF_8.name());
    }

}

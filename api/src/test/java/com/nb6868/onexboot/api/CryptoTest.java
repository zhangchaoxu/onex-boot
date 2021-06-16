package com.nb6868.onexboot.api;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 加解密 测试
 * see https://hutool.cn/docs/#/crypto/%E6%A6%82%E8%BF%B0
 *
 * @author Charles zhangchaoxu@gmail.com
 */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CryptoTest {

    @Test
    @DisplayName("二维码")
    public void aesTest() {
        String test1 = "{\"username\":\"1\",\"password\":\"1\",\"uuid\":\"\",\"captcha\":\"\",\"type\":10}";
        String key = "1234567890adbcde";
        byte[] encrypt = SecureUtil.aes(key.getBytes()).encrypt(test1);
        String base64 = Base64.encode(encrypt);
        System.out.println("数据：" + test1);
        System.out.println("加密：" + base64);
        System.out.println("解密：" + SecureUtil.aes(key.getBytes()).decryptStr(Base64.decode(base64)));
    }

}

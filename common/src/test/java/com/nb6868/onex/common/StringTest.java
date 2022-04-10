package com.nb6868.onex.common;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.pojo.EncryptForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

@DisplayName("IP测试")
@Slf4j
public class StringTest {

    @Test
    @DisplayName("string fmt")
    void matchIp() {
        String result = StrUtil.format("{}{}SSS", "111", StrUtil.nullToEmpty(null));
        log.error("result={}", result);
    }

    @Test
    @DisplayName("split")
    void split() {
        String result = null;
        StrUtil.splitTrim(result, ',').forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                log.error("result={}", s);
            }
        });
    }

    @Test
    @DisplayName("aesEncode")
    void aesEncodeTest() {
        String raw = "{\"password\":\"admin\",\"tenantCode\": \"xxx\",\"type\":\"ADMIN_USERNAME_PASSWORD\",\"username\":\"admin\"}";
        String content = SecureUtil.aes(Const.AES_KEY.getBytes()).encryptBase64(raw);
        EncryptForm form = new EncryptForm();
        form.setBody(content);
        log.error("form=" + form);
    }

    @Test
    @DisplayName("aesDecode")
    void aesDecodeTest() {
        String raw = "bqJOFdO/IlOCMHJ6V+BDpyVlY1N5opy5uOrww4u/6huTIK7XB5WVAGiYflVn5AmzeLCpaiXQxUorBW3P05kexppCz3Y8uSi7W7NpWWWc7wY3OOT4aLKLZwylNiorEz5S";
        EncryptForm form = new EncryptForm();
        form.setBody(raw);
        String json = SecureUtil.aes(Const.AES_KEY.getBytes()).decryptStr(form.getBody());
        log.error("json=" + json);
    }

    @Test
    @DisplayName("passwordTest")
    void passwordTest() {
        String reg = "^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";
        String p1 = "admin@2022";
        String p2 = "admin2022";
        String p3 = "admin@@";
        String p4 = "123456##";
        String p5 = "admiadmin";
        String p6 = "1234567999";
        log.error("p1=" + ReUtil.isMatch(reg, p1));
        log.error("p2=" + ReUtil.isMatch(reg, p2));
        log.error("p3=" + ReUtil.isMatch(reg, p3));
        log.error("p4=" + ReUtil.isMatch(reg, p4));
        log.error("p5=" + ReUtil.isMatch(reg, p5));
        log.error("p6=" + ReUtil.isMatch(reg, p6));
    }

}

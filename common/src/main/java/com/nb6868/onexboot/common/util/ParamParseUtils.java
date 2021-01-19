package com.nb6868.onexboot.common.util;

import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 网络请求参数解析工具
 *
 * @author zhangchaoxu@gmail.com
 */
public class ParamParseUtils {

    @SneakyThrows
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("~", "%7E")
                .replace("/", "%2F");
    }

    @SneakyThrows
    public static String hmacSHA1Sign(String accessSecret, String stringToSign) {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8.name()), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8.name()));
        return Base64.getEncoder().encodeToString(signData);
    }

}

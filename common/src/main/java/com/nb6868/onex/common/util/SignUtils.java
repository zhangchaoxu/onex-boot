package com.nb6868.onex.common.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.crypto.SecureUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 签名工具类
 *
 * @author zhangchaoxu@gmail.com
 */
@Slf4j
public class SignUtils {

    /**
     * MD5加密
     *
     * @param data 明文
     * @return 密文
     */
    public static String signMd5(String data) {
        return SecureUtil.md5(data);
    }

    /**
     * 加密
     *
     * @param data 明文
     * @param key 密钥
     * @param algorithm 算法 如:HmacSHA1/HmacSHA256
     * @return 密文
     */
    public static String signToBase64(String data, String key, String algorithm) {
        try {
            // 加密
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
            mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8.name()), algorithm));
            byte[] signData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8.name()));
            // base64
            return java.util.Base64.getEncoder().encodeToString(signData);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            log.error("SignUtils sign error", e);
            return null;
        }
    }

    public static String paramToQueryString(Map<String, Object> params) {
        return paramToQueryString(params, "&", "=");
    }

    /**
     * 参数转为字符串
     *
     * @param params            参数
     * @param paramDelimiter    参数与参数之间分隔符 默认&
     * @param keyValueDelimiter 参数键值之间分隔符 默认=
     * @return 拼接后的内容
     */
    public static String paramToQueryString(Map<String, Object> params, String paramDelimiter, String keyValueDelimiter) {
        StrJoiner stringJoiner = new StrJoiner(paramDelimiter);
        // 参数KEY排序
        MapUtil.sort(params).forEach((key, value) -> stringJoiner.append(urlEncode(key) + keyValueDelimiter + urlEncode(value.toString())));
        return stringJoiner.toString();
    }

    /**
     * 特殊urlEncode
     */
    @SneakyThrows
    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("~", "%7E")
                .replace("/", "%2F");
    }

}

package com.nb6868.onex.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阿里云基础接口接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class BaseAliyunApi {

    // ISO 8601 format
    public static final String ISO8601_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ACS3_HMAC_SHA256 = "ACS3-HMAC-SHA256";

    /**
     * V3签名
     * <a href="https://help.aliyun.com/zh/sdk/product-overview/v3-request-structure-and-signature">V3版本签名</a>
     */
    public static String signV3(HttpRequest request, String accessKeySecret) {
        UrlBuilder requestUrl = UrlBuilder.ofHttp(request.getUrl(), Charset.forName(request.charset()));
        // 步骤1：构造CanonicalRequest
        String canonicalRequest =
                // HTTP Verb
                request.getMethod().name() + StrUtil.LF +
                        // Canonical URI
                        urlEncode(URLUtil.decode(requestUrl.getPathStr()), true) + StrUtil.LF;
        // Canonical Query String，针对QueryString排序UriEncode后的字符串，单独对key和value进行编码
        canonicalRequest += getSortedQueryString(requestUrl.getQuery() == null ? null : requestUrl.getQuery().getQueryMap());
        canonicalRequest += StrUtil.LF;
        // Canonical Headers
        canonicalRequest += "host:" + requestUrl.getHost() + StrUtil.LF +
                // x-oss-* sort
                getSortedXHeader(request.headers()) +
                // 上面的LF表示单个参数的结束，整个LF表示整段header的结束
                StrUtil.LF +
                // SignedHeaders + '\n' +  // 已签名消息头
                "host;x-acs-action;x-acs-content-sha256;x-acs-date;x-acs-signature-nonce;x-acs-version" + StrUtil.LF +
                // RequestBody经过hash处理后的值HashedRequestPayload
                SecureUtil.sha256("");

        // log.error("CanonicalRequest={}", canonicalRequest);
        // 步骤2：构造待签名字符串（StringToSign）
        String stringToSign =
                // 签名哈希算法
                ACS3_HMAC_SHA256 + StrUtil.LF +
                        // sha256
                        SecureUtil.sha256(canonicalRequest);

        // 步骤3：计算Signature。
        byte[] result = SecureUtil.hmacSha256(accessKeySecret).digest(stringToSign);
        return HexUtil.encodeHexStr(result);
    }

    public static String buildAuthorization(String assessKeyId, String signature) {
        return StrUtil.format("{} Credential={},SignedHeaders=host;x-acs-action;x-acs-content-sha256;x-acs-date;x-acs-signature-nonce;x-acs-version,Signature={}", ACS3_HMAC_SHA256, assessKeyId, signature);
    }

    /**
     * 排序后的x-*
     * Header的key必须小写，value必须经过Trim（去除头尾的空格）。
     * 按Header中key的字典序进行排列。
     */
    private static String getSortedXHeader(Map<String, List<String>> headers) {
        // 过滤出x-oss-开头的header
        Map<String, List<String>> filterMap = new HashMap<>();
        headers.forEach((key, strings) -> {
            if (StrUtil.startWithAnyIgnoreCase(key, "x-")) {
                filterMap.put(key, strings);
            }
        });
        StrJoiner result = new StrJoiner("");
        MapUtil.sort(filterMap).forEach((key, value) -> result.append(key.toLowerCase() + ":" + CollUtil.join(value, ";") + StrUtil.LF));
        return result.toString();
    }

    /**
     * 针对QueryString执行UriEncode后的字符串，单独对key和value进行编码。
     * 按QueryString的key进行排序，先编码，再排序。如果有多个相同的key，按照原来添加的顺序放置即可。
     */
    private static String getSortedQueryString(Map<CharSequence, CharSequence> map) {
        // 过滤出x-oss-开头的header
        Map<String, String> filterMap = new HashMap<>();
        MapUtil.defaultIfEmpty(map, new HashMap<>()).forEach((key, strings) -> filterMap.put(URLEncodeUtil.encode(StrUtil.nullToEmpty(key)), URLEncodeUtil.encodeAll(StrUtil.nullToEmpty(strings))));
        StrJoiner result = new StrJoiner("&");
        MapUtil.sort(filterMap).forEach((key, value) -> result.append(key + "=" + value));
        return result.toString();
    }

    /**
     * 阿里云的弱智urlEncode
     */
    public static String urlEncode(String value, boolean ignoreSlashes) {
        String encoded = URLEncoder.encode(value, Charset.defaultCharset());
        encoded = StrUtil.replace(encoded, "+", "%20");
        encoded = StrUtil.replace(encoded, "*", "%2A");
        encoded = StrUtil.replace(encoded, "%7E", "~");
        if (ignoreSlashes) {
            encoded = StrUtil.replace(encoded, "%2F", "/");
        }
        return encoded;
    }

}

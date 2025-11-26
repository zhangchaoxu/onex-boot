package com.nb6868.onex.msg.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 阿里云短信接口
 *
 * <a href="https://help.aliyun.com/zh/sdk/product-overview/v3-request-structure-and-signature">使用V3签名</a>
 */
@Slf4j
public class AliyunSmsApi {

    // ISO 8601 format
    public static final String ISO8601_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ACS3_HMAC_SHA256 = "ACS3-HMAC-SHA256";

    /**
     * 发送短信(单个)
     */
    public static ApiResult<JSONObject> sendSms(String accessKeyId, String accessKeySecret, String endPoint, Map<String, Object> paras) {
        ApiResult<JSONObject> apiResult = ApiResult.of(null);
        if (StrUtil.hasBlank(accessKeyId, accessKeySecret)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        Date date = DateUtil.date();
        String nonce = IdUtil.fastSimpleUUID();
        String url = HttpUtil.urlWithForm(endPoint, paras, Charset.defaultCharset(), false);
        HttpRequest request = HttpRequest.of(url)
                .method(Method.POST)
                .header("x-acs-action", "SendSms")
                .header("x-acs-version", "2017-05-25")
                .header("x-acs-signature-nonce", nonce)
                // RequestBody经过Hash摘要处理后再进行Base16编码的结果，与HashedRequestPayload相一致。
                .header("x-acs-content-sha256", SecureUtil.sha256(""))
                .header("x-acs-date", DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT"))));
        String sign = signV3(request, accessKeySecret);
        request.header("Authorization", buildAuthorization(accessKeyId, sign));
        try {
            // log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject result = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(StrUtil.equalsIgnoreCase("OK", JSONUtil.getByPath(result, "Code", "")))
                        .setCode(JSONUtil.getByPath(result, "Code", ""))
                        .setMsg(JSONUtil.getByPath(result, "Message", ""))
                        .setData(result);
            });
            return apiResult;
        } catch (HttpException he) {
            log.error("aliyun sms api sendsms http exception", he);
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + he.getMessage()).setRetry(true);
        } catch (JSONException je) {
            log.error("aliyun sms api sendsms json exception", je);
            return apiResult.error(ApiResult.ERROR_CODE_JSON_EXCEPTION, url + "=>http exception=>" + je.getMessage()).setRetry(true);
        } catch (Exception e) {
            log.error("aliyun sms api sendsms error", e);
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage()).setRetry(true);
        }
    }

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

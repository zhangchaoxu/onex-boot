package com.nb6868.onex.common.oss;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 阿里云OSS接口服务
 *
 * <a href="https://help.aliyun.com/zh/oss/developer-reference/overview-24">使用REST API向OSS发起请求</a>
 */
@Slf4j
public class AliyunOssApi {

    // ISO 8601 format
    public static final String ISO8601_DATETIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    public static final String ISO8601_DATETIME_MS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss:SSS'Z'";
    public static final String ISO8601_DATE_FORMAT = "yyyyMMdd";

    public static final String OSS4_HMAC_SHA256 = "OSS4-HMAC-SHA256";
    public static final String TERMINATOR = "aliyun_v4_request";
    public static final String SECRET_KEY_PREFIX = "aliyun_v4";

    /**
     * 获得预授权地址,支持put和get
     * 在URL中包含V4签名
     * <a href="https://help.aliyun.com/zh/oss/use-cases/uploading-objects-to-oss-directly-from-clients">在客户端直接上传文件到OSS</a>
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/add-signatures-to-urls">在URL中包含V4签名</a>
     *
     * @param expires 签名URL的有效时长，单位为秒（s）。最小值为1，最大值为 604800
     */
    public static ApiResult<String> getPreSignedUrl(String accessKeyId, String accessKeySecret, String endPoint, String region, String bucketName, String objectKey, Map<String, Object> objectMetadataMap, String method, int expires) {
        ApiResult<String> apiResult = ApiResult.of(null);
        if (StrUtil.hasBlank(accessKeyId, accessKeySecret, bucketName, objectKey)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        Date date = DateUtil.date();
        // 时间格式化
        String dateFmt1 = DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT")));
        String dateFmt2 = DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT")));

        List<String> additionalHeaders = new ArrayList<>();
        additionalHeaders.add("host");
        // https://examplebucket.oss-cn-hangzhou.aliyuncs.com/exampleobject
        // ?x-oss-signature-version=OSS4-HMAC-SHA256
        // &x-oss-credential=<AccessKeyId>/20231203/cn-hangzhou/oss/aliyun_v4_request
        // &x-oss-date=20231203T121212Z
        // &x-oss-expires=86400
        // &x-oss-additional-headers=host
        // &x-oss-signature=<signature-to-be-calculated>
        String queryString = StrUtil.format("x-oss-additional-headers={}&" +
                        "x-oss-credential={}%2F{}%2F{}%2Foss%2F{}&" +
                        "x-oss-date={}&" +
                        "x-oss-expires={}&" +
                        "x-oss-signature-version={}",
                StrUtil.join(";", ListUtil.sort(additionalHeaders, String::compareTo)),
                accessKeyId, dateFmt2, region, TERMINATOR,
                dateFmt1,
                expires,
                OSS4_HMAC_SHA256
        );
        String url = StrUtil.format("http://{}.{}/{}", bucketName, endPoint, objectKey);
        HttpRequest request = HttpRequest.of(url + "?" + queryString).method(Method.valueOf(method.toUpperCase()));
        ObjUtil.defaultIfNull(objectMetadataMap, new HashMap<String, Object>()).forEach((key, value) -> {
            // 从传参获取header值
            request.header(key, String.valueOf(value));
        });
        if (StrUtil.isBlank(request.header(Header.CONTENT_TYPE))) {
            request.contentType(StrUtil.emptyToDefault(FileUtil.getMimeType(objectKey), ContentType.OCTET_STREAM.getValue()));
        }
        String sign = signV4(request, date, bucketName, region, additionalHeaders, accessKeySecret);
        return apiResult.success(URLEncodeUtil.encode(url) + "?" + queryString + "&x-oss-signature=" + sign);
    }

    /**
     * GetObject接口用于获取某个文件（Object）
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/getobject">...</a>
     */
    public static ApiResult<InputStream> getObject(String accessKeyId, String accessKeySecret, String endPoint, String region, String bucketName, String objectKey, Map<String, Object> objectMetadataMap) {
        ApiResult<InputStream> apiResult = ApiResult.of(null);
        if (StrUtil.hasBlank(accessKeyId, accessKeySecret, bucketName, objectKey)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        Date date = DateUtil.date();
        String url = StrUtil.format("http://{}.{}/{}", bucketName, endPoint, objectKey);
        HttpRequest request = HttpRequest.of(url)
                .method(Method.GET)
                .header("x-oss-content-sha256", "UNSIGNED-PAYLOAD")
                .header("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"))));
        ObjUtil.defaultIfNull(objectMetadataMap, new HashMap<String, Object>()).forEach((key, value) -> {
            // 从传参获取header值
            request.header(key, String.valueOf(value));
        });
        List<String> additionalHeaders = new ArrayList<>();
        additionalHeaders.add("host");
        String sign = signV4(request, date, bucketName, region, additionalHeaders, accessKeySecret);
        request.header("Authorization", buildAuthorization(date, accessKeyId, region, additionalHeaders, sign));
        try {
            log.debug(request.toString());
            request.then(httpResponse -> {
                if (httpResponse.isOk()) {
                    InputStream inputStream = IoUtil.toAvailableStream(httpResponse.bodyStream());
                    apiResult.setSuccess(true)
                            .setCode("ok")
                            .setMsg("ok")
                            .setData(inputStream);
                } else {
                    String xOssErr = httpResponse.header("x-oss-err");
                    JSONObject result = JSONUtil.xmlToJson(Base64.decodeStr(xOssErr));
                    apiResult.setSuccess(false)
                            .setCode(JSONUtil.getByPath(result, "Error.Code", ""))
                            .setMsg(JSONUtil.getByPath(result, "Error.Message", ""))
                            .setData(null);
                }
            });
            return apiResult;
        } catch (HttpException he) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + he.getMessage()).setRetry(true);
        } catch (JSONException je) {
            return apiResult.error(ApiResult.ERROR_CODE_JSON_EXCEPTION, url + "=>http exception=>" + je.getMessage()).setRetry(true);
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * 调用PutObject接口上传文件（Object）
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/putobject">...</a>
     */
    public static ApiResult<JSONObject> putObject(String accessKeyId, String accessKeySecret, String endPoint, String region, String bucketName, String objectKey, Map<String, Object> objectMetadataMap, InputStream inputStream) {
        ApiResult<JSONObject> apiResult = ApiResult.of(null);
        if (StrUtil.hasBlank(accessKeyId, accessKeySecret, bucketName, objectKey)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        Date date = DateUtil.date();
        String url = StrUtil.format("http://{}.{}/{}", bucketName, endPoint, objectKey);
        HttpRequest request = HttpRequest.of(url)
                .method(Method.PUT)
                .header("x-oss-content-sha256", "UNSIGNED-PAYLOAD")
                .header("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"))))
                .body(IoUtil.readBytes(inputStream));
        ObjUtil.defaultIfNull(objectMetadataMap, new HashMap<String, Object>()).forEach((key, value) -> {
            // 从传参获取header值
            request.header(key, String.valueOf(value));
        });
        if (StrUtil.isBlank(request.header(Header.CONTENT_TYPE))) {
            request.contentType(StrUtil.emptyToDefault(FileUtil.getMimeType(objectKey), ContentType.OCTET_STREAM.getValue()));
        }
        List<String> additionalHeaders = new ArrayList<>();
        additionalHeaders.add("host");
        String sign = signV4(request, date, bucketName, region, additionalHeaders, accessKeySecret);
        request.header("Authorization", buildAuthorization(date, accessKeyId, region, additionalHeaders, sign));
        try {
            log.debug(request.toString());
            request.then(httpResponse -> {
                if (httpResponse.isOk()) {
                    JSONObject result = new JSONObject();
                    httpResponse.headers().forEach((key, values) -> {
                        if (StrUtil.isNotBlank(key) && CollUtil.isNotEmpty(values)) {
                            result.set(key, CollUtil.join(values, ";"));
                        }
                    });
                    result.set("objectKey", objectKey);
                    apiResult.setSuccess(true)
                            .setCode("ok")
                            .setMsg("ok")
                            .setData(result);
                } else {
                    String xOssErr = httpResponse.header("x-oss-err");
                    JSONObject result = JSONUtil.xmlToJson(Base64.decodeStr(xOssErr));
                    apiResult.setSuccess(false)
                            .setCode(JSONUtil.getByPath(result, "Error.Code", ""))
                            .setMsg(JSONUtil.getByPath(result, "Error.Message", ""))
                            .setData(result);
                }
            });
            return apiResult;
        } catch (HttpException he) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + he.getMessage()).setRetry(true);
        } catch (JSONException je) {
            return apiResult.error(ApiResult.ERROR_CODE_JSON_EXCEPTION, url + "=>http exception=>" + je.getMessage()).setRetry(true);
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * HeadObject接口用于获取某个文件（Object）的元数据
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/headobject">...</a>
     */
    public static ApiResult<JSONObject> headObject(String accessKeyId, String accessKeySecret, String endPoint, String region, String bucketName, String objectKey, Map<String, Object> objectMetadataMap) {
        ApiResult<JSONObject> apiResult = ApiResult.of(null);
        if (StrUtil.hasBlank(accessKeyId, accessKeySecret, bucketName, objectKey)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        Date date = DateUtil.date();
        String url = StrUtil.format("http://{}.{}/{}", bucketName, endPoint, objectKey);
        HttpRequest request = HttpRequest.of(url)
                .method(Method.HEAD)
                .header("x-oss-content-sha256", "UNSIGNED-PAYLOAD")
                .header("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"))));
        ObjUtil.defaultIfNull(objectMetadataMap, new HashMap<String, Object>()).forEach((key, value) -> {
            // 从传参获取header值
            request.header(key, String.valueOf(value));
        });
        List<String> additionalHeaders = new ArrayList<>();
        additionalHeaders.add("host");
        String sign = signV4(request, date, bucketName, region, additionalHeaders, accessKeySecret);
        request.header("Authorization", buildAuthorization(date, accessKeyId, region, additionalHeaders, sign));
        try {
            log.debug(request.toString());
            request.then(httpResponse -> {
                if (httpResponse.isOk()) {
                    JSONObject result = new JSONObject();
                    httpResponse.headers().forEach((key, values) -> {
                        if (StrUtil.isNotBlank(key) && CollUtil.isNotEmpty(values)) {
                            result.set(key, CollUtil.join(values, ";"));
                        }
                    });
                    apiResult.setSuccess(true)
                            .setCode("ok")
                            .setMsg("ok")
                            .setData(result);
                } else {
                    String xOssErr = httpResponse.header("x-oss-err");
                    JSONObject result = JSONUtil.xmlToJson(Base64.decodeStr(xOssErr));
                    apiResult.setSuccess(false)
                            .setCode(JSONUtil.getByPath(result, "Error.Code", ""))
                            .setMsg(JSONUtil.getByPath(result, "Error.Message", ""))
                            .setData(result);
                }
            });
            return apiResult;
        } catch (HttpException he) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + he.getMessage()).setRetry(true);
        } catch (JSONException je) {
            return apiResult.error(ApiResult.ERROR_CODE_JSON_EXCEPTION, url + "=>http exception=>" + je.getMessage()).setRetry(true);
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * POST V4签名
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/signature-version-4-recommend">POST V4签名</a>
     */
    public static String postSignV4(Date date, String region, String accessKeySecret, JSONObject policy) {
        // 时间格式化
        String dateFmt2 = DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT")));
        // 步骤1：创建policy。
        // 步骤2：构造待签名字符串（StringToSign）。
        String stringToSign = Base64.encode(policy.toString());
        // 步骤3：计算SigningKey。
        byte[] dateKey = hmacSha256((SECRET_KEY_PREFIX + accessKeySecret).getBytes(), dateFmt2);
        byte[] dateRegionKey = hmacSha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacSha256(dateRegionKey, "oss");
        byte[] signingKey = hmacSha256(dateRegionServiceKey, TERMINATOR);
        // 步骤4：计算Signature。
        byte[] result = hmacSha256(signingKey, stringToSign);
        return HexUtil.encodeHexStr(result);
    }

    /**
     * V4签名
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/recommend-to-use-signature-version-4">在Header中包含V4签名</a>
     */
    public static String signV4(HttpRequest request, Date date, String bucketName, String region, List<String> additionalHeaders, String accessKeySecret) {
        // 时间格式化
        String dateFmt1 = DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT")));
        String dateFmt2 = DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT")));
        UrlBuilder requestUrl = UrlBuilder.ofHttp(request.getUrl(), Charset.forName(request.charset()));
        // 步骤1：构造CanonicalRequest
        String canonicalRequest =
                // HTTP Verb
                request.getMethod().name() + StrUtil.LF +
                        // Canonical URI
                        StrUtil.SLASH + bucketName + requestUrl.getPathStr() + StrUtil.LF;
        // Canonical Query String，针对QueryString排序UriEncode后的字符串，单独对key和value进行编码
        canonicalRequest += getSortedQueryString(requestUrl.getQuery() == null ? null : requestUrl.getQuery().getQueryMap());
        canonicalRequest += StrUtil.LF;
        // Canonical Headers
        if (StrUtil.isNotBlank(request.header(Header.CONTENT_TYPE))) {
            canonicalRequest += "content-type:" + StrUtil.trim(request.header(Header.CONTENT_TYPE)) + StrUtil.LF;
        }
        if (StrUtil.isNotBlank(request.header("Content-MD5"))) {
            canonicalRequest += "content-md5:" + StrUtil.trim(request.header("Content-MD5")) + StrUtil.LF;
        }
        canonicalRequest += "host:" + requestUrl.getHost() + StrUtil.LF +
                // x-oss-* sort
                getSortedXOssHeader(request.headers()) +
                // 上面的LF表示单个参数的结束，整个LF表示整段header的结束
                StrUtil.LF +
                // Additional Headers
                StrUtil.join(";", ListUtil.sort(additionalHeaders, String::compareTo)) + StrUtil.LF +
                // Hashed PayLoad
                "UNSIGNED-PAYLOAD";

        // log.info("canonicalRequest={}", canonicalRequest);

        // 步骤2：构造待签名字符串（StringToSign）
        String stringToSign =
                // 签名哈希算法
                OSS4_HMAC_SHA256 + StrUtil.LF +
                        // timestampe
                        dateFmt1 + StrUtil.LF +
                        // scope
                        dateFmt2 + StrUtil.SLASH + region + StrUtil.SLASH + "oss" + StrUtil.SLASH + TERMINATOR + StrUtil.LF +
                        // sha256
                        SecureUtil.sha256(canonicalRequest);

        // 步骤3：计算Signature。
        byte[] dateKey = hmacSha256((SECRET_KEY_PREFIX + accessKeySecret).getBytes(), dateFmt2);
        byte[] dateRegionKey = hmacSha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacSha256(dateRegionKey, "oss");
        byte[] signingKey = hmacSha256(dateRegionServiceKey, TERMINATOR);
        // 步骤4：计算Signature。
        byte[] result = hmacSha256(signingKey, stringToSign);
        return HexUtil.encodeHexStr(result);
    }

    public static String buildAuthorization(Date date, String assessKeyId, String region, List<String> additionalHeaders, String signature) {
        String dateFmt2 = DateUtil.format(date, FastDateFormat.getInstance(ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT")));
        return StrUtil.format("{} Credential={}/{}/{}/oss/{},AdditionalHeaders={},Signature={}", OSS4_HMAC_SHA256, assessKeyId, dateFmt2, region, TERMINATOR, StrUtil.join(";", ListUtil.sort(additionalHeaders, String::compareTo)), signature);
    }

    /**
     * 排序后的x-oss-*
     * Header的key必须小写，value必须经过Trim（去除头尾的空格）。
     * 按Header中key的字典序进行排列。
     */
    private static String getSortedXOssHeader(Map<String, List<String>> headers) {
        // 过滤出x-oss-开头的header
        Map<String, List<String>> filterMap = new HashMap<>();
        headers.forEach((key, strings) -> {
            if (StrUtil.startWithAnyIgnoreCase(key, "x-oss-")) {
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
     * 计算HMacSha256
     */
    private static byte[] hmacSha256(byte[] key, String data) {
        return SecureUtil.hmacSha256(key).digest(data);
    }

}

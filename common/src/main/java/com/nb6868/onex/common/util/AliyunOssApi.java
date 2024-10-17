package com.nb6868.onex.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 阿里云OSS接口工具类
 * <a href="https://help.aliyun.com/zh/oss/developer-reference/list-of-operations-by-function">文档</a>
 * <Error>
 * <Code>InvalidArgument</Code>
 * <Message>Invalid signing region in Authorization header.</Message>
 * <RequestId>xx</RequestId>
 * <HostId>xxx.oss-cn-hangzhou.aliyuncs.com</HostId>
 * <Authorization></Authorization>
 * <EC>0002-00000226</EC>
 * <RecommendDoc>https://api.aliyun.com/troubleshoot?q=0002-00000226</RecommendDoc>
 * </Error>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AliyunOssApi {

    /**
     * 调用PutObject接口上传文件（Object）
     */
    public static ApiResult putObject(String assessKeyId, String accessKeySecret, String endPoint, String bucketName, String objectName, File file) {
        return null;
    }

    /**
     * V4签名
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/recommend-to-use-signature-version-4">在Header中包含V4签名</a>
     */
    public static String signV4(HttpRequest request, Date date, String bucketName, String region, String assessKeyId, String accessKeySecret) {
        // 时间格式化
        String dateFmt1 = DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"), Locale.US));
        String dateFmt2 = DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd", TimeZone.getTimeZone("GMT"), Locale.US));

        UrlBuilder requestUrl = UrlBuilder.ofHttp(request.getUrl(), Charset.forName(request.charset()));
        // 步骤1：构造CanonicalRequest
        String canonicalRequest =
                // HTTP Verb
                request.getMethod().name() + StrUtil.LF +
                        // Canonical URI
                        "/" + bucketName + requestUrl.getPathStr() + StrUtil.LF +
                        // Canonical Query String
                        requestUrl.getQueryStr() + StrUtil.LF +
                        // Canonical Headers
                        "content-type:" + request.header(Header.CONTENT_TYPE).toLowerCase() + StrUtil.LF;
        if (StrUtil.isNotBlank(request.header("Content-MD5"))) {
            canonicalRequest += "content-md5:" + StrUtil.trim(request.header("Content-MD5")) + StrUtil.LF;
        }
        canonicalRequest += "host:" + requestUrl.getHost() + StrUtil.LF +
                // x-oss-* sort
                getSortedXOssHeader(request.headers()) +
                // 上面的LF表示单个参数的结束，整个LF表示整段header的结束
                StrUtil.LF +
                // Additional Headers
                "host" + StrUtil.LF +
                // Hashed PayLoad
                "UNSIGNED-PAYLOAD";

        log.error("canonicalRequest={}", canonicalRequest);

        // 步骤2：构造待签名字符串（StringToSign）
        String stringToSign =
                // 签名哈希算法
                "OSS4-HMAC-SHA256" + StrUtil.LF +
                        // timestampe
                        dateFmt1 + StrUtil.LF +
                        // scope
                        dateFmt2 + "/" + region + "/oss/aliyun_v4_request" + StrUtil.LF +
                        // sha256
                        SecureUtil.sha256(canonicalRequest);

        // 步骤3：计算Signature。
        byte[] dateKey = hmacSha256(("aliyun_v4" + accessKeySecret).getBytes(), dateFmt2);
        byte[] dateRegionKey = hmacSha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacSha256(dateRegionKey, "oss");
        byte[] signingKey = hmacSha256(dateRegionServiceKey, "aliyun_v4_request");
        byte[] result = hmacSha256(signingKey, stringToSign);
        String signature = HexUtil.encodeHexStr(result);

        return StrUtil.format("OSS4-HMAC-SHA256 Credential={}/{}/{}/oss/aliyun_v4_request,AdditionalHeaders=host,Signature={}", assessKeyId, dateFmt2, region, signature);
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
     * 计算HMacSha256
     */
    private static byte[] hmacSha256(byte[] key, String data) {
        return SecureUtil.hmacSha256(key).digest(data);
    }

}

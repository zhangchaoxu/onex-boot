package com.nb6868.onex.common.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 阿里云存储
 * see <a href="https://help.aliyun.com/document_detail/32008.html">...</a>
 *
 * 如何配置访问OSS文件时是预览行为
 * see <a href="https://help.aliyun.com/zh/oss/user-guide/how-to-ensure-an-object-is-previewed-when-you-access-the-object">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class AliyunApiOssService extends AbstractOssService {

    public AliyunApiOssService(OssPropsConfig config) {
        this.config = config;
    }

    /**
     * 上传文件
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/putobject">...</a>
     */
    @Override
    public String upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        Date date = DateUtil.date();
        HttpRequest request = HttpRequest.put(StrUtil.format("http://{}.oss-{}.aliyuncs.com/{}", this.config.getBucketName(), this.config.getRegion(), objectKey))
                //.header("Host", "xxx-dc.oss-cn-hangzhou.aliyuncs.com")
                //.header("Date", DateUtil.format(date, DatePattern.HTTP_DATETIME_FORMAT))
                .header("x-oss-content-sha256", "UNSIGNED-PAYLOAD")
                .header("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"))))
                .body(IoUtil.readBytes(inputStream));
        ObjUtil.defaultIfNull(objectMetadataMap, new HashMap<String, Object>()).forEach((key, value) -> {
            // 从传参获取header值
            request.header(key, String.valueOf(value));
        });
        if (StrUtil.isBlank(request.header(Header.CONTENT_TYPE))) {
            request.contentType(StrUtil.emptyToDefault(FileUtil.getMimeType(objectKey), "text/plain"));
        }
        String sign = signV4(request, date, this.config.getBucketName(), this.config.getRegion(), this.config.getAccessKeyId(), this.config.getAccessKeySecret());
        request.header("Authorization", sign);
        HttpResponse response;
        try {
            response = request.execute();
        } catch (Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e.getMessage());
        }
        if (response.isOk()) {
            // 成功
            return config.getDomain() + objectKey;
        } else {
            // 不成功，解析错误
            Document resultDoc = XmlUtil.parseXml(response.body());
            Object code = XmlUtil.getByXPath("//Error/Code", resultDoc, XPathConstants.STRING);
            Object message = XmlUtil.getByXPath("//Error/Message", resultDoc, XPathConstants.STRING);
            log.error("code={},msg={}", code, message);
            log.error("res.body={}", response.body());
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, code.toString() + ":" + message.toString());
        }
    }

    @Override
    public InputStream download(String objectKey) {
        return null;
    }


    @Override
    public String getPresignedUrl(String objectName, Long expire) {
        return "";
    }

    @Override
    public JSONObject getSts() {
       return null;
    }

    /**
     * 获取文件元数据
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/headobject">...</a>
     */
    @Override
    public boolean isObjectKeyExisted(String bucketName, String objectKey) {
        Date date = DateUtil.date();
        HttpRequest request = HttpRequest.head(StrUtil.format("http://{}.oss-{}.aliyuncs.com/{}", this.config.getBucketName(), this.config.getRegion(), objectKey))
                //.header("Host", "xxx-dc.oss-cn-hangzhou.aliyuncs.com")
                //.header("Date", DateUtil.format(date, DatePattern.HTTP_DATETIME_FORMAT))
                .header("x-oss-content-sha256", "UNSIGNED-PAYLOAD")
                .header("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"))));

        String sign = signV4(request, date, this.config.getBucketName(), this.config.getRegion(), this.config.getAccessKeyId(), this.config.getAccessKeySecret());
        request.header("Authorization", sign);
        HttpResponse response;
        try {
            response = request.execute();
        } catch (Exception e) {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e.getMessage());
        }
        if (response.isOk()) {
            // 成功
            return true;
        } else {
            // 不成功，解析错误
            Document resultDoc = XmlUtil.parseXml(response.body());
            Object code = XmlUtil.getByXPath("//Error/Code", resultDoc, XPathConstants.STRING);
            Object message = XmlUtil.getByXPath("//Error/Message", resultDoc, XPathConstants.STRING);
            if (StrUtil.containsAny(code.toString(), "NoSuchKey", "SymlinkTargetNotExist")) {
                return false;
            }
            log.error("code={},msg={}", code, message);
            log.error("res.body={}", response.body());
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, code.toString() + ":" + message.toString());
        }
    }

    /**
     * V4签名
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/recommend-to-use-signature-version-4">在Header中包含V4签名</a>
     */
    public static String signV4(HttpRequest request, Date date, String bucketName, String region, String assessKeyId, String accessKeySecret) {
        // 时间格式化
        String dateFmt1 = DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT")));
        String dateFmt2 = DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd", TimeZone.getTimeZone("GMT")));
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

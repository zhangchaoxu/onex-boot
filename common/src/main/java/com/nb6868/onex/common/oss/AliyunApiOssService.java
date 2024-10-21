package com.nb6868.onex.common.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * 阿里云存储
 * see <a href="https://help.aliyun.com/document_detail/32008.html">...</a>
 * <p>
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
        ApiResult<JSONObject> result = AliyunOssApi.putObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getBucketName(), this.config.getRegion(), objectKey, null, inputStream);
        if (result.isSuccess()) {
            return config.getDomain() + objectKey;
        } else {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, result.getCodeMsg());
        }
    }

    @Override
    public InputStream download(String objectKey) {
        ApiResult<InputStream> result = AliyunOssApi.getObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getBucketName(), this.config.getRegion(), objectKey, null);
        if (result.isSuccess()) {
            return result.getData();
        } else {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, result.getCodeMsg());
        }
    }

    @Override
    public String getPresignedUrl(String objectKey, String method, Long expiration) {
        // http://${bucketName}.oss-cn-hangzhou.aliyuncs.com/${objectKey}?Expires=${失效时间秒时间戳}&OSSAccessKeyId=${AccessKeyId}&Signature=${sign}
        Date date = DateUtil.date();
        HttpRequest request = HttpRequest.of(StrUtil.format("http://{}.oss-{}.aliyuncs.com/{}", this.config.getBucketName(), this.config.getRegion(), objectKey))
                .method(Method.valueOf(method.toUpperCase()))
                //.header("Host", "xxx-dc.oss-cn-hangzhou.aliyuncs.com")
                //.header("Date", DateUtil.format(date, DatePattern.HTTP_DATETIME_FORMAT))
                .header("x-oss-expires", String.valueOf(expiration))
                .header("x-oss-content-sha256", "UNSIGNED-PAYLOAD")
                .header("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("GMT"))));

        /*String sign = AliyunOssApi.getPreSignedUrl(request, date, this.config.getBucketName(), this.config.getRegion(), this.config.getAccessKeyId(), this.config.getAccessKeySecret());
        return StrUtil.format("http://{}.oss-cn-hangzhou.aliyuncs.com/${objectKey}?Expires=${失效时间秒时间戳}&OSSAccessKeyId=${AccessKeyId}&Signature=${sign}");*/
        return null;
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
        ApiResult<JSONObject> result = AliyunOssApi.headObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getBucketName(), this.config.getRegion(), objectKey, null);
        if (result.isSuccess()) {
            return true;
        } else if (StrUtil.containsAny(result.getCode(), "NoSuchKey", "SymlinkTargetNotExist")) {
            return false;
        } else {
            throw new OnexException(ErrorCode.OSS_UPLOAD_FILE_ERROR, result.getCodeMsg());
        }
    }

}

package com.nb6868.onex.common.oss;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * 阿里云存储
 * see <a href="https://help.aliyun.com/document_detail/32008.html">...</a>
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
    public ApiResult<JSONObject> upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        ApiResult<JSONObject> result = AliyunOssApi.putObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getRegion(), this.config.getBucketName(), objectKey, null, inputStream);
        return result;
    }

    @Override
    public ApiResult<InputStream> download(String objectKey) {
        ApiResult<InputStream> result = AliyunOssApi.getObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getRegion(), this.config.getBucketName(), objectKey, null);
        return result;
    }

    @Override
    public ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire) {
        ApiResult<String> result = AliyunOssApi.getPreSignedUrl(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), StrUtil.emptyToDefault(this.config.getEndPointPublic(), this.config.getEndPoint()), this.config.getRegion(), this.config.getBucketName(), objectKey, null, method, expire);
        return result;
    }

    @Override
    public ApiResult<JSONObject> getSignedPostForm(JSONArray conditions, int expire) {
        Date date = DateUtil.date();
        JSONObject policy = new JSONObject();
        // https://help.aliyun.com/zh/oss/developer-reference/signature-version-4-recommend
        // 用于指定policy的过期时间，以ISO8601 GMT时间表示
        policy.set("expiration", DateUtil.format(DateUtil.offsetSecond(date, expire), FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATETIME_MS_FORMAT, TimeZone.getTimeZone("GMT"))));
        // 指定POST请求表单域的合法值
        conditions = ObjUtil.defaultIfNull(conditions, new JSONArray());
        conditions.add(new JSONObject().set("bucket", this.config.getBucketName()));
        conditions.add(new JSONObject().set("x-oss-signature-version", AliyunOssApi.OSS4_HMAC_SHA256));
        conditions.add(new JSONObject().set("x-oss-credential", StrUtil.format("{}/{}/{}/oss/aliyun_v4_request", this.config.getAccessKeyId(), DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT"))), this.config.getRegion())));
        conditions.add(new JSONObject().set("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT")))));
        policy.set("conditions", conditions);
        // 签名
        String sign = AliyunOssApi.postSignV4(date, this.config.getRegion(), this.config.getAccessKeySecret(), policy);
        JSONObject result = new JSONObject();
        // 访问地址
        result.set("host",  StrUtil.format("{}.{}", this.config.getBucketName(), StrUtil.emptyToDefault(this.config.getEndPointPublic(), this.config.getEndPoint())));
        // header
        JSONObject form = new JSONObject();
        form.set("policy", Base64.encode(policy.toString()));
        form.set("x-oss-signature-version", AliyunOssApi.OSS4_HMAC_SHA256);
        form.set("x-oss-credential", StrUtil.format("{}/{}/{}/oss/aliyun_v4_request", this.config.getAccessKeyId(), DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATE_FORMAT, TimeZone.getTimeZone("GMT"))), this.config.getRegion()));
        form.set("x-oss-date", DateUtil.format(date, FastDateFormat.getInstance(AliyunOssApi.ISO8601_DATETIME_FORMAT, TimeZone.getTimeZone("GMT"))));
        form.set("x-oss-signature", sign);
        result.set("form", form);
        // result.set("key", objectKey);
        // result.set("file", file);
        return new ApiResult<JSONObject>().success(result);
    }

    /**
     * 获取文件元数据
     * <a href="https://help.aliyun.com/zh/oss/developer-reference/headobject">...</a>
     */
    @Override
    public ApiResult<Boolean> isObjectKeyExisted(String bucketName, String objectKey) {
        ApiResult<JSONObject> result = AliyunOssApi.headObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getRegion(), this.config.getBucketName(), objectKey, null);
        return new ApiResult<Boolean>().setData(result.isSuccess());
    }

}

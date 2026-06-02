package com.nb6868.onex.common.oss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;

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
    public ApiResult<String> getPreSignedUrl(String objectKey, String urlParams, String method, int expire) {
        ApiResult<String> result = AliyunOssApi.getPreSignedUrl(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), StrUtil.emptyToDefault(this.config.getEndPointPublic(), this.config.getEndPoint()), this.config.getRegion(), this.config.getBucketName(), objectKey, urlParams, null, method, expire);
        return result;
    }

    public ApiResult<String> getWebOfficeUrl(String objectKey, String urlParams, String method, int expire) {
        ApiResult<String> result = AliyunOssApi.getWebOfficeUrl(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getDomain(), this.config.getRegion(), this.config.getBucketName(), objectKey, urlParams, null, method, expire);
        return result;
    }

    /**
     * 异步处理
     */
    public ApiResult<JSONObject> asyncProcessObject(String objectKey, String params) {
        return AliyunOssApi.asyncProcessObject(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), this.config.getEndPoint(), this.config.getRegion(), this.config.getBucketName(), objectKey, null, params);
    }

    @Override
    public ApiResult<JSONObject> getSignedPostForm(JSONArray conditions, int expire, String objectKey) {
        ApiResult<JSONObject> result = AliyunOssApi.getSignedPostForm(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), StrUtil.emptyToDefault(this.config.getEndPointPublic(), this.config.getEndPoint()), this.config.getRegion(), this.config.getBucketName(), objectKey, conditions, expire, this.config.getDomain());
        return result;
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

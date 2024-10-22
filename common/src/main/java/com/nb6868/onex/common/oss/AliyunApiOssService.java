package com.nb6868.onex.common.oss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
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
    public ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire) {
        ApiResult<String> result = AliyunOssApi.getPreSignedUrl(this.config.getAccessKeyId(), this.config.getAccessKeySecret(), StrUtil.emptyToDefault(this.config.getEndPointPublic(), this.config.getEndPoint()), this.config.getRegion(), this.config.getBucketName(), objectKey, null, method, expire);
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

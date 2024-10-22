package com.nb6868.onex.common.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.ApiResult;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * 本地上传
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LocalOssService extends AbstractOssService {

    public LocalOssService(OssPropsConfig config) {
        this.config = config;
    }

    @Override
    public ApiResult<JSONObject> upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadataMap) {
        ApiResult<JSONObject> apiResult = new ApiResult<>();
        File localFile = new File(config.getBucketName() + File.separator + objectKey);
        new FileWriter(localFile).writeFromStream(inputStream, true);
        return apiResult.success(new JSONObject());
    }

    @Override
    public ApiResult<InputStream> download(String objectKey) {
        ApiResult<InputStream> apiResult = new ApiResult<>();
        File localFile = new File(config.getBucketName() + File.separator + objectKey);
        if (localFile.exists()) {
            apiResult.setData(IoUtil.toStream(localFile));
        } else {
            apiResult.error(ApiResult.ERROR_CODE_PARAMS, "文件不存在");
        }
        return apiResult;
    }

    @Override
    public ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire) {
        return new ApiResult<String >().error(ApiResult.ERROR_CODE_EXCEPTION, "local oss getPreSignedUrl 未实现");
    }

    @Override
    public ApiResult<Boolean> isObjectKeyExisted(String bucketName, String objectKey) {
        // 给一个默认值，免得出错
        ApiResult<Boolean> apiResult = ApiResult.of(false);
        apiResult.setData(FileUtil.exist(bucketName + File.separator +  objectKey));
        return apiResult;
    }
}

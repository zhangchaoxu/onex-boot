package com.nb6868.onex.msg.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.util.BaseAliyunApi;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 阿里云短信接口
 *
 * <a href="https://help.aliyun.com/zh/sdk/product-overview/v3-request-structure-and-signature">使用V3签名</a>
 */
@Slf4j
public class AliyunSmsApi extends BaseAliyunApi {

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

}

package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 阿里云验证码校验接口
 *
 * <a href="https://help.aliyun.com/zh/captcha/captcha2-0/use-cases/server-api-access">HTTPS接入服务端</a>
 */
@Slf4j
public class AliyunCaptchaApi extends BaseAliyunApi {

    /**
     * 检验验证码
     */
    public static ApiResult<JSONObject> verifyIntelligentCaptcha(String accessKeyId, String accessKeySecret, String endPoint, Map<String, Object> paras) {
        ApiResult<JSONObject> apiResult = ApiResult.of(null);
        if (StrUtil.hasBlank(accessKeyId, accessKeySecret)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        Date date = DateUtil.date();
        String nonce = IdUtil.fastSimpleUUID();
        String url = endPoint;//
        // 手动补全，以/结尾
        if (StrUtil.endWith(url, "/")) {
            url += "/";
        }
        HttpRequest request = HttpRequest.of(url)
                .method(Method.POST)
                // API请求参数以application/x-www-form-urlencoded表单形式
                .form(paras)
                .header("x-acs-action", "VerifyIntelligentCaptcha")
                .header("x-acs-version", "2023-03-05")
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
                apiResult.setSuccess(StrUtil.equalsIgnoreCase("Success", JSONUtil.getByPath(result, "Code", "")))
                        .setCode(JSONUtil.getByPath(result, "Code", ""))
                        .setMsg(JSONUtil.getByPath(result, "Message", ""))
                        .setData(result);
            });
            return apiResult;
        } catch (HttpException he) {
            log.error("aliyun VerifyIntelligentCaptcha api http exception", he);
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + he.getMessage()).setRetry(true);
        } catch (JSONException je) {
            log.error("aliyun VerifyIntelligentCaptcha api json exception", je);
            return apiResult.error(ApiResult.ERROR_CODE_JSON_EXCEPTION, url + "=>http exception=>" + je.getMessage()).setRetry(true);
        } catch (Exception e) {
            log.error("aliyun VerifyIntelligentCaptcha api error", e);
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage()).setRetry(true);
        }
    }

}

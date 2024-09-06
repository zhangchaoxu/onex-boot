package com.nb6868.onex.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 基础调用方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class BaseApi {

    /**
     * 公共基础调用方法
     *
     * @param baseUrl 请求连接
     * @param paramMap 请求参数,会拼接到url中
     */
    public static ApiResult<JSONObject> baseCallApiGet(String baseUrl, JSONObject paramMap) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (StrUtil.isBlank(baseUrl)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS, "参数不能为空");
        }
        cn.hutool.core.date.TimeInterval timeInterval = DateUtil.timer();
        // 将参数拼接到url上
        String url = HttpUtil.urlWithFormUrlEncoded(baseUrl, paramMap, Charset.defaultCharset());
        try {
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getBool("success", false))
                        .setCode(resultJson.getStr("code"))
                        .setMsg(resultJson.getStr("msg"))
                        .setData(resultJson);
            });
            log.info("url params={},time:{}", HttpUtil.toParams(paramMap, null, false), timeInterval.intervalPretty());
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
     * 公共基础调用方法
     *
     * @param baseUrl 请求连接
     * @param paramMap 请求参数
     */
    public static ApiResult<JSONObject> baseCallApiPostJson(String baseUrl, JSONObject paramMap) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (StrUtil.isBlank(baseUrl)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        cn.hutool.core.date.TimeInterval timeInterval = DateUtil.timer();
        // 将参数拼接到url上
        String url = baseUrl;
        try {
            HttpRequest request = HttpRequest.post(url)
                    .body(paramMap.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getBool("success", false))
                        .setCode(resultJson.getStr("code"))
                        .setMsg(resultJson.getStr("msg"))
                        .setData(resultJson);
            });
            log.info("url params={},time:{}", HttpUtil.toParams(paramMap, null, false), timeInterval.intervalPretty());
            return apiResult;
        } catch (HttpException he) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + he.getMessage()).setRetry(true);
        } catch (JSONException je) {
            return apiResult.error(ApiResult.ERROR_CODE_JSON_EXCEPTION, url + "=>http exception=>" + je.getMessage()).setRetry(true);
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage()).setRetry(true);
        }
    }

}

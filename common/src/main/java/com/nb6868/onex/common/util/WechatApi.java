package com.nb6868.onex.common.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;

public class WechatApi {

    // token缓存，默认7200s
    static TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7200 * DateUnit.SECOND.getMillis());

    public static final String BASE_URL = "https://api.weixin.qq.com";

    /**
     * 获取稳定版接口调用凭据
     * <a href="https://developers.weixin.qq.com/doc/service/api/base/api_getstableaccesstoken.html">...</a>
     */
    public static ApiResult<String> getStableAccessToken(String grant_type, String appid, String secret, boolean force_refresh) {
        ApiResult<String> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(grant_type, appid, secret)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        if (!force_refresh && StrUtil.isNotBlank(tokenCache.get(appid, false))) {
            apiResult.success(tokenCache.get(appid, false));
            return apiResult;
        }
        // 将参数拼接到url上
        String url = BASE_URL + "/cgi-bin/stable_token";
        JSONObject formBody = new JSONObject()
                .set("grant_type", grant_type)
                .set("appid", appid)
                .set("secret", secret)
                .set("force_refresh", force_refresh);
        HttpRequest request = HttpRequest.post(url).body(formBody.toString());
        try (HttpResponse response = request.execute()) {
            JSONObject resultJson = JSONUtil.parseObj(response.body());
            apiResult.setSuccess(resultJson.getInt("errcode", -2) == 0)
                    .setCode(resultJson.getStr("errcode"))
                    .setMsg(resultJson.getStr("errmsg"))
                    .setData(resultJson.getStr("access_token"));
            if (apiResult.isSuccess() && StrUtil.isNotBlank(apiResult.getData())) {
                // 如果
                tokenCache.put("appid", apiResult.getData(), DateUnit.SECOND.getMillis() * resultJson.getInt("expires_in"));
            } else {
                // 理论上不会到这里
                apiResult.setSuccess(false);
            }
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * 生成带参数的二维码
     * <a href="https://developers.weixin.qq.com/doc/service/api/qrcode/qrcodes/api_createqrcode.html">...</a>
     */
    public static ApiResult<JSONObject> createQRCode(String access_token, Integer expire_seconds, String action_name, JSONObject action_info) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (StrUtil.hasBlank(access_token, action_name)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        // 将参数拼接到url上
        String url = BASE_URL + "/cgi-bin/qrcode/create?access_token=" + access_token;
        JSONObject formBody = new JSONObject()
                .set("expire_seconds", expire_seconds)
                .set("action_name", action_name)
                .set("action_info", action_info);
        HttpRequest request = HttpRequest.post(url).body(formBody.toString());
        try (HttpResponse response = request.execute()) {
            JSONObject resultJson = JSONUtil.parseObj(response.body());
            apiResult.setSuccess(resultJson.getInt("errcode", -2) == 0)
                    .setCode(resultJson.getStr("errcode"))
                    .setMsg(resultJson.getStr("errmsg"))
                    .setData(resultJson);
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * 获取sdk临时票据
     * <a href="https://developers.weixin.qq.com/doc/service/api/webdev/jssdk/api_getticket.html">...</a>
     */
    public static ApiResult<JSONObject> getTicket(String access_token, String type) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (StrUtil.hasBlank(access_token, type)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        // 将参数拼接到url上
        String url = StrUtil.format(BASE_URL + "/cgi-bin/ticket/getticket?access_token={}&type={}", access_token, type);
        HttpRequest request = HttpRequest.get(url);
        try (HttpResponse response = request.execute()) {
            JSONObject resultJson = JSONUtil.parseObj(response.body());
            apiResult.setSuccess(resultJson.getInt("errcode", -2) == 0)
                    .setCode(resultJson.getStr("errcode"))
                    .setMsg(resultJson.getStr("errmsg"))
                    .setData(resultJson);
            /**
             * {
             *   "errcode":0,
             *   "errmsg":"ok",
             *   "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKdvsdshFKA",
             *   "expires_in":7200
             * }
             */
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * 换取用户授权凭证
     * <a href="https://developers.weixin.qq.com/doc/service/api/webdev/access/api_snsaccesstoken.html">...</a>
     */
    public static ApiResult<JSONObject> snsAccessToken(String appid, String secret, String code) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (StrUtil.hasBlank(appid, secret, code)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        // 将参数拼接到url上
        String url = StrUtil.format(BASE_URL + "/sns/oauth2/access_token?appid={}&secret={}&code={}&grant_type={}", appid, secret, code, "authorization_code");
        HttpRequest request = HttpRequest.get(url);
        try (HttpResponse response = request.execute()) {
            JSONObject resultJson = JSONUtil.parseObj(response.body());
            apiResult.setSuccess(resultJson.getInt("errcode", -2) == 0)
                    .setCode(resultJson.getStr("errcode"))
                    .setMsg(resultJson.getStr("errmsg"))
                    .setData(resultJson);
            /**
             * {
             *   "access_token": "ACCESS_TOKEN",
             *   "expires_in": 7200,
             *   "refresh_token": "REFRESH_TOKEN",
             *   "openid": "OPENID",
             *   "unionid": "UNIONID",
             *   "is_snapshotuser": 1
             * }
             */
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + e.getMessage()).setRetry(true);
        }
    }

    /**
     * 获取授权用户信息
     * <a href="https://developers.weixin.qq.com/doc/service/api/webdev/access/api_snsuserinfo.html">...</a>
     */
    public static ApiResult<JSONObject> snsUserInfo(String access_token, String openid, String lang) {
        ApiResult<JSONObject> apiResult = ApiResult.of(new JSONObject());
        if (StrUtil.hasBlank(access_token, openid)) {
            return apiResult.error(ApiResult.ERROR_CODE_PARAMS);
        }
        // 将参数拼接到url上
        String url = StrUtil.format(BASE_URL + "/sns/userinfo?access_token={}&openid={}&lang={}", access_token, openid, StrUtil.blankToDefault(lang, "zh_CN"));
        HttpRequest request = HttpRequest.get(url);
        try (HttpResponse response = request.execute()) {
            JSONObject resultJson = JSONUtil.parseObj(response.body());
            apiResult.setSuccess(resultJson.getInt("errcode", -2) == 0)
                    .setCode(resultJson.getStr("errcode"))
                    .setMsg(resultJson.getStr("errmsg"))
                    .setData(resultJson);
            /**
             * {
             *   "openid": "OPENID",
             *   "nickname": NICKNAME,
             *   "sex": 1,
             *   "province":"PROVINCE",
             *   "city":"CITY",
             *   "country":"COUNTRY",
             *   "headimgurl":"https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
             *   "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
             *   "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
             * }
             */
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ApiResult.ERROR_CODE_HTTP_EXCEPTION, url + "=>http exception=>" + e.getMessage()).setRetry(true);
        }
    }
}

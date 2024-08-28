package com.nb6868.onex.common.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.core.io.FileSystemResource;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * 钉钉接口工具类
 * <a href="https://open.dingtalk.com/document/orgapp/learning-map">文档</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class DingTalkApiUtils {

    // token 缓存,有效时间2小时
    static TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7200 * 1000);

    private static final String ACS_TOKEN_KEY = "x-acs-dingtalk-access-token";
    private static final String BASE_URL = "https://oapi.dingtalk.com";
    private static final String BASE_URL2 = "https://api.dingtalk.com";

    /**
     * 清空token缓存
     *
     * @param appKey
     */
    public static void removeTokenCache(String appKey) {
        tokenCache.remove(appKey);
    }

    /**
     * 清空token缓存
     */
    public static void clearTokenCache() {
        tokenCache.clear();
    }

    /**
     * 获取企业内部应用的access_token（旧版本）
     * <a href="https://ding-doc.dingtalk.com/document/app/obtain-orgapp-token">...</a>
     */
    @Deprecated
    public static ApiResult<?> getAccessToken(String appKey, String appSecret, boolean refresh) {
        // 三元组结果
        if (StrUtil.isBlank(appKey)) {
            return ApiResult.of().error("500001", "参数不能为空");
        }
        String token = tokenCache.get(appKey, false);
        if (refresh || StrUtil.isBlank(token)) {
            // 强制刷新,或者缓存为空
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/gettoken", Dict.create().set("appkey", appKey).set("appsecret", appSecret), Charset.defaultCharset(), true);
                HttpRequest request = HttpRequest.get(url);
                log.debug(request.toString());
                ApiResult apiResult =  ApiResult.of();
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                            .setCode(resultJson.getStr("errcode"))
                            .setMsg(resultJson.getStr("errmsg"))
                            .setData(resultJson.getStr("access_token"));
                });
                return apiResult;
            } catch (Exception e) {
                return ApiResult.of().error("500002", "gettoken:" + e.getMessage());
            }
        } else {
            return ApiResult.of().success("get token from cache", token);
        }
    }

    /**
     * 获取企业内部应用的access_token（新版本）
     * <a href="https://open.dingtalk.com/document/orgapp/obtain-the-access_token-of-an-internal-app">...</a>
     */
    public static Triple<Boolean, String, String> getOauth2AccessToken(String appKey, String appSecret, boolean refresh) {
        // 三元组结果
        MutableTriple<Boolean, String, String> triple = new MutableTriple<>(false, null, null);
        String token = tokenCache.get(appKey, false);
        if (refresh || StrUtil.isBlank(token)) {
            // 强制刷新,或者缓存为空
            try {
                JSONObject formBody = new JSONObject().set("appkey", appKey).set("appsecret", appSecret);
                HttpRequest request = HttpRequest.post(BASE_URL2 + "/v1.0/oauth2/accessToken")
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    // 可以用response.state == 200或者内容中是否有目标来判断结果
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    if (StrUtil.isNotBlank(resultJson.getStr("accessToken"))) {
                        triple.setLeft(true);
                        triple.setRight(resultJson.getStr("accessToken"));
                        tokenCache.put(appKey, resultJson.getStr("accessToken"));
                    } else {
                        triple.setLeft(false);
                        triple.setMiddle(resultJson.getStr("code") + ":" + resultJson.getStr("message"));
                    }
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("dingtalk.v1.0/oauth2/accessToken error:" + e.getMessage());
            }
        } else {
            triple.setLeft(true);
            triple.setMiddle("get token from cache");
            triple.setRight(token);
        }
        return triple;
    }

    /**
     * 获取用户token
     * <a href="https://open.dingtalk.com/document/isvapp/obtain-user-token">...</a>
     */
    public static Triple<Boolean, String, String> getUserAccessToken(String clientId, String clientSecret, String code) {
        // 三元组结果
        MutableTriple<Boolean, String, String> triple = new MutableTriple<>(false, null, null);
        try {
            JSONObject formBody = new JSONObject().set("clientId", clientId).set("clientSecret", clientSecret).set("code", code).set("grantType", "authorization_code");
            HttpRequest request = HttpRequest.post(BASE_URL2 + "/v1.0/oauth2/userAccessToken")
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                // 可以用response.state == 200或者内容中是否有目标来判断结果
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                if (StrUtil.isNotBlank(resultJson.getStr("accessToken"))) {
                    triple.setLeft(true);
                    triple.setRight(resultJson.getStr("accessToken"));
                } else {
                    triple.setLeft(false);
                    triple.setMiddle(resultJson.getStr("code") + ":" + resultJson.getStr("message"));
                }
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dingtalk.v1.0/oauth2/userAccessToken error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 通过临时授权码获取授权用户的个人信息
     * <a href="https://ding-doc.dingtalk.com/document/app/obtain-the-user-information-based-on-the-sns-temporary-authorization">...</a>
     */
    public static Triple<Boolean, String, JSONObject> getUserInfoByCode(String appId, String appSecret, String code) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = SignUtils.signToBase64(timestamp, appSecret, "HmacSHA256");
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/sns/getuserinfo_bycode", Dict.create().set("accessKey", appId).set("timestamp", timestamp).set("signature", SignUtils.urlEncode(signature)), Charset.defaultCharset(), true);
            JSONObject formBody = new JSONObject().set("tmp_auth_code", code);
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("user_info"));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dingtalk.sns/getuserinfo_bycode error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 通过免登码获取用户信息
     * 1. <a href="https://developers.dingtalk.com/document/app/logon-free-process">...</a>
     * 2. <a href="https://developers.dingtalk.com/document/app/obtain-the-userid-of-a-user-by-using-the-log-free">...</a>
     */
    public static Triple<Boolean, String, JSONObject> getUserInfoV2ByCode(String accessKey, String appSecret, String code) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/getuserinfo", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject formBody = new JSONObject().set("code", code);
                HttpRequest request = HttpRequest
                        .post(url)
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    triple.setLeft(resultJson.getInt("errcode") == 0);
                    triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                    triple.setRight(resultJson.getJSONObject("result"));
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("dingtalk.topapi/v2/user/getuserinfo error:" + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 获取用户通讯录个人信息
     * <a href="https://open.dingtalk.com/document/isvapp/dingtalk-retrieve-user-information">...</a>
     */
    public static Triple<Boolean, String, JSONObject> getUserContact(String accessToken, String unionId) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        if (StrUtil.hasBlank(accessToken, unionId)) {
            triple.setMiddle("参数不能为空");
            return triple;
        }
        try {
            HttpRequest request = HttpRequest.post(BASE_URL2 + "/v1.0/contact/users/" + unionId)
                    .header(ACS_TOKEN_KEY, accessToken);
            log.debug(request.toString());
            request.then(httpResponse -> {
                // 可以用response.state == 200或者内容中是否有目标来判断结果
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(HttpStatus.HTTP_OK == httpResponse.getStatus());
                if (triple.getLeft()) {
                    triple.setRight(resultJson);
                } else {
                    triple.setMiddle(resultJson.getStr("code") + ":" + resultJson.getStr("message"));
                }
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dingtalk.v1.0/contact/users/ error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 根据unionid获取用户userid
     * <a href="https://ding-doc.dingtalk.com/document/app/query-a-user-by-the-union-id">...</a>
     */
    public static Triple<Boolean, String, JSONObject> getUserIdByUnionid(String accessKey, String appSecret, String unionid) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/user/getbyunionid", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject formBody = new JSONObject().set("unionid", unionid);
                HttpRequest request = HttpRequest
                        .post(url)
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    triple.setLeft(resultJson.getInt("errcode") == 0);
                    triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                    triple.setRight(resultJson.getJSONObject("result"));
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("dingtalk.topapi/user/getbyunionid error:" + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 根据userid获取用户详情
     * <a href="https://ding-doc.dingtalk.com/document/app/query-user-details">...</a>
     */
    public static Triple<Boolean, String, JSONObject> getUserDetailByUserId(String accessKey, String appSecret, String userid) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/get", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject formBody = new JSONObject().set("userid", userid).set("language", "zh_CN");
                HttpRequest request = HttpRequest
                        .post(url)
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    triple.setLeft(resultJson.getInt("errcode") == 0);
                    triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                    triple.setRight(resultJson.getJSONObject("result"));
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("dingtalk.topapi/v2/user/get error:" + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 自定义机器人消息发送
     * <a href="https://developers.dingtalk.com/document/robots/custom-robot-access">...</a>
     */
    public static Triple<Boolean, String, JSONObject> sendRobotMsg(String accessToken, JSONObject formBody) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, new JSONObject());
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/robot/send", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("robot/send接口调用失败:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 发送工作通知
     * <a href="https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages">...</a>
     */
    public static Triple<Boolean, String, JSONObject> sendNotifyMsg(String accessKey, String appSecret, JSONObject formBody) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/message/corpconversation/asyncsend_v2", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                HttpRequest request = HttpRequest
                        .post(url)
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    triple.setLeft(resultJson.getInt("errcode") == 0);
                    triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                    triple.setRight(resultJson);
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("notify/send接口调用失败:" + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 注册回调事件
     * <a href="https://developers.dingtalk.com/document/app/registers-event-callback-interfaces">...</a>
     */
    public static Triple<Boolean, String, JSONObject> registerCallback(String accessToken, String aesKey, String token, String callbackUrl, String[] callbackTag) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            JSONObject formBody = new JSONObject()
                    .set("aes_key", aesKey)
                    .set("token", token)
                    .set("url", callbackUrl)
                    .set("call_back_tag", callbackTag);
            String url = HttpUtil.urlWithForm(BASE_URL + "/call_back/register_call_back", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson);
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dingtalk.call_back/register_call_back error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 上传媒体文件
     * <a href="https://developers.dingtalk.com/document/app/upload-media-files">...</a>
     */
    public static Triple<Boolean, String, JSONObject> uploadMedia(String accessToken, String type, String filePath) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/media/upload", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest
                    .post(url)
                    .form("type", type)
                    .form("media", new FileSystemResource(filePath));
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("result"));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dingtalk.media.upload error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * ASR 一句话语音识别
     * <a href="https://developers.dingtalk.com/document/app/asr-short-sentence-recognition">...</a>
     */
    public static Triple<Boolean, String, JSONObject> asrVoiceTranslate(String mediaId, String accessToken) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            JSONObject formBody = new JSONObject()
                    .set("media_id", mediaId);
            String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/asr/voice/translate", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("result"));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dinttalk,topapi/asr/voice/translate error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * OCR文字识别
     * <a href="https://developers.dingtalk.com/document/app/structured-image-recognition-api">...</a>
     */
    public static Triple<Boolean, String, String> ocrStructuredRecognize(String type, String mediaUrl, String accessToken) {
        // 三元组结果
        MutableTriple<Boolean, String, String> triple = new MutableTriple<>(false, null, null);
        try {
            JSONObject formBody = new JSONObject()
                    .set("type", type)
                    .set("mediaUrl", mediaUrl);
            String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/ocr/structured/recognize", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getStr("result"));
            });
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("dingtalk.topapi/asr/voice/translate error:" + e.getMessage());
        }
        return triple;
    }

    /**
     * 根据部门id获得子部门id数组
     */
    public static Triple<Boolean, String, List<Integer>> getDeptIdList(String accessKey, String appSecret, String deptId) {
        // 三元组结果
        MutableTriple<Boolean, String, List<Integer>> triple = new MutableTriple<>(false, null, new ArrayList<>());
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                JSONObject formBody = new JSONObject().set("dept_id", deptId);
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/department/listsubid", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                HttpRequest request = HttpRequest
                        .post(url)
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    triple.setLeft(resultJson.getInt("errcode") == 0);
                    triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                    triple.setRight(JSONUtil.getByPath(resultJson, "result.dept_id_list", new ArrayList<>()));
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("dingtalk.topapi/v2/department/listsubid error" + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 根据部门id获得子部门id数组
     */
    public static Triple<Boolean, String, JSONObject> getUserListByDeptId(String accessKey, String appSecret, JSONObject formBody) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/list", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                HttpRequest request = HttpRequest
                        .post(url)
                        .body(formBody.toString());
                log.debug(request.toString());
                request.then(httpResponse -> {
                    JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                    triple.setLeft(resultJson.getInt("errcode") == 0);
                    triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                    triple.setRight(resultJson.getJSONObject("result"));
                });
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("dingtalk.topapi/v2/department/listsubid error:" + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 获得所有的部门id,钉钉sb接口不能一次性获取所有部门,也不支持级联获取。
     * 使用triple封装结果，避免异常的丢失，返回结果参考Result,分别是success、msg、deptIdList
     * <a href="https://open.dingtalk.com/document/orgapp/obtain-a-sub-department-id-list-v2">...</a>
     */
    public static Triple<Boolean, String, List<Integer>> getAllDeptIdList(String accessKey, String appSecret) {
        // 三元组结果
        MutableTriple<Boolean, String, List<Integer>> triple = new MutableTriple<>(false, null, new ArrayList<>());
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            // 初始化的时候把根id放进去，用户可能会挂载根上
            List<Integer> departmemtIdList = CollUtil.newArrayList(1);
            // 根部门dept_id传1
            List<Integer> dept_id_list = CollUtil.newArrayList(1);
            // 逐级遍历
            while (!dept_id_list.isEmpty()) {
                // 初始化一个新的数组存储结果
                List<Integer> dept_sub_id_list = CollUtil.newArrayList();
                dept_id_list.forEach(deptId -> {
                    try {
                        JSONObject formBody = new JSONObject().set("dept_id", deptId);
                        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/department/listsubid", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                        HttpRequest request = HttpRequest
                                .post(url)
                                .body(formBody.toString());
                        log.debug(request.toString());
                        request.then(httpResponse -> dept_sub_id_list.addAll(JSONUtil.getByPath(JSONUtil.parseObj(httpResponse.body()), "result.dept_id_list", new ArrayList<>())));
                    } catch (Exception e) {
                        log.error("dingtalk./topapi/v2/department/listsubid error", e);
                    }
                });
                // 反馈给循环条件
                dept_id_list = dept_sub_id_list;
                // 塞入结果数组
                departmemtIdList.addAll(dept_sub_id_list);
            }
            // 塞入三元组
            triple.setLeft(true);
            triple.setRight(departmemtIdList);
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 获得部门下所有用户列表，钉钉sb接口,用户挂在根部门上
     * 使用triple封装结果，避免异常的丢失
     * <a href="https://open.dingtalk.com/document/orgapp/queries-the-complete-information-of-a-department-user">...</a>
     */
    public static Triple<Boolean, String, List<JSONObject>> getUserListByDeptIds(String accessKey, String appSecret, List<Integer> deptIds) {
        // 三元组结果
        MutableTriple<Boolean, String, List<JSONObject>> triple = new MutableTriple<>(false, null, new ArrayList<>());
        // token结果
        Triple<Boolean, String, String> tokenResponse = getOauth2AccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            List<JSONObject> userList = new ArrayList<>();
            deptIds.forEach(deptId -> {
                AtomicInteger cursor = new AtomicInteger();
                while (cursor.get() >= 0) {
                    try {
                        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/list", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                        JSONObject formBody = new JSONObject()
                                .set("dept_id", deptId)
                                .set("cursor", cursor)
                                .set("size", 10);
                        HttpRequest request = HttpRequest
                                .post(url)
                                .body(formBody.toString());
                        log.debug(request.toString());
                        request.then(httpResponse -> {
                            JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                            cursor.set(JSONUtil.getByPath(resultJson, "result.next_cursor", -1));
                            userList.addAll(JSONUtil.getByPath(resultJson, "result.list", new ArrayList<>()));
                        });
                    } catch (Exception e) {
                        log.error("dingtalk.topapi/v2/user/list error", e);
                        cursor.set(-1);
                    }
                }
            });
            // 塞入三元组
            triple.setLeft(true);
            // 通过userid去重
            triple.setRight(CollUtil.distinct(userList, (Function<JSONObject, Object>) entries -> entries.getStr("userid"), true));
        } else {
            triple.setLeft(false);
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

}

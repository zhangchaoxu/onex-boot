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
    private static TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7200 * 1000);
    private static final String ACS_TOKEN_KEY = "x-acs-dingtalk-access-token";
    private static final String BASE_URL = "https://oapi.dingtalk.com";
    private static final String BASE_URL2 = "https://api.dingtalk.com";
    // 错误码,异常
    public static final String ERROR_CODE_EXCEPTION = "500500";
    // 错误码,请求参数问题
    public static final String ERROR_CODE_BAD_REQUEST = "500400";

    /**
     * 清空token缓存
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
    public static ApiResult<String> getAccessToken(String appKey, String appSecret, boolean refresh) {
        ApiResult<String> apiResult = ApiResult.of();
        if (StrUtil.isBlank(appKey)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String token = tokenCache.get(appKey, false);
        if (!refresh && StrUtil.isNotBlank(token)) {
            return apiResult.success("token from cache", token);
        }
        // 强制刷新,或者缓存为空
        String url = HttpUtil.urlWithForm(BASE_URL + "/gettoken", Dict.create().set("appkey", appKey).set("appsecret", appSecret), Charset.defaultCharset(), true);
        try {
            HttpRequest request = HttpRequest.get(url);
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getStr("access_token"));
                if (apiResult.isSuccess()) {
                    tokenCache.put(appKey, resultJson.getStr("access_token"));
                }
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 获取企业内部应用的access_token（新版本）
     * <a href="https://open.dingtalk.com/document/orgapp/obtain-the-access_token-of-an-internal-app">...</a>
     */
    public static ApiResult<String> getOauth2AccessToken(String appKey, String appSecret, boolean refresh) {
        ApiResult<String> apiResult = ApiResult.of();
        if (StrUtil.isBlank(appKey)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String token = tokenCache.get(appKey, false);
        if (!refresh && StrUtil.isNotBlank(token)) {
            return apiResult.success("token from cache", token);
        }
        // 强制刷新,或者缓存为空
        String url = BASE_URL2 + "/v1.0/oauth2/accessToken";
        JSONObject formBody = new JSONObject().set("appKey", appKey).set("appSecret", appSecret);
        try {
            HttpRequest request = HttpRequest.post(url).body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                // 可以用response.state == 200或者内容中是否有目标来判断结果
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(StrUtil.isNotBlank(resultJson.getStr("accessToken")))
                        .setCode(resultJson.getStr("code"))
                        .setMsg(resultJson.getStr("message"))
                        .setData(resultJson.getStr("accessToken"));
                if (apiResult.isSuccess()) {
                    tokenCache.put(appKey, resultJson.getStr("accessToken"));
                }
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 获取用户token
     * <a href="https://open.dingtalk.com/document/isvapp/obtain-user-token">...</a>
     */
    public static ApiResult<String> getUserAccessToken(String clientId, String clientSecret, String code) {
        ApiResult<String> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(clientId, clientId, clientId)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = BASE_URL2 + "/v1.0/oauth2/userAccessToken";
        JSONObject formBody = new JSONObject().set("clientId", clientId).set("clientSecret", clientSecret).set("code", code).set("grantType", "authorization_code");
        try {
            HttpRequest request = HttpRequest.post(url).body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                // 可以用response.state == 200或者内容中是否有目标来判断结果
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(StrUtil.isNotBlank(resultJson.getStr("accessToken")))
                        .setCode(resultJson.getStr("code"))
                        .setMsg(resultJson.getStr("message"))
                        .setData(resultJson.getStr("accessToken"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 通过临时授权码获取授权用户的个人信息
     * <a href="https://ding-doc.dingtalk.com/document/app/obtain-the-user-information-based-on-the-sns-temporary-authorization">...</a>
     */
    public static ApiResult<JSONObject> getUserInfoByCode(String appId, String appSecret, String code) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(appId, appSecret, code)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        // 处理参数
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = SignUtils.signToBase64(timestamp, appSecret, "HmacSHA256");
        String url = HttpUtil.urlWithForm(BASE_URL + "/sns/getuserinfo_bycode", Dict.create().set("accessKey", appId).set("timestamp", timestamp).set("signature", SignUtils.urlEncode(signature)), Charset.defaultCharset(), true);
        JSONObject formBody = new JSONObject().set("tmp_auth_code", code);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("user_info"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 通过免登码获取用户信息
     * 1. <a href="https://developers.dingtalk.com/document/app/logon-free-process">...</a>
     * 2. <a href="https://developers.dingtalk.com/document/app/obtain-the-userid-of-a-user-by-using-the-log-free">...</a>
     */
    public static ApiResult<JSONObject> getUserInfoV2ByCode(String accessToken, String code) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, code)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/getuserinfo", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        JSONObject formBody = new JSONObject().set("code", code);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 获取用户通讯录个人信息
     * <a href="https://open.dingtalk.com/document/isvapp/dingtalk-retrieve-user-information">...</a>
     */
    public static ApiResult<JSONObject> getUserContact(String accessToken, String unionId) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, unionId)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = BASE_URL2 + "/v1.0/contact/users/" + unionId;
        try {
            HttpRequest request = HttpRequest.post(url)
                    .header(ACS_TOKEN_KEY, accessToken);
            log.debug(request.toString());
            request.then(httpResponse -> {
                // 可以用response.state == 200或者内容中是否有目标来判断结果
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(HttpStatus.HTTP_OK == httpResponse.getStatus())
                        .setCode(resultJson.getStr("code"))
                        .setMsg(resultJson.getStr("message"))
                        .setData(resultJson);
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 根据unionid获取用户userid
     * <a href="https://ding-doc.dingtalk.com/document/app/query-a-user-by-the-union-id">...</a>
     */
    public static ApiResult<JSONObject> getUserIdByUnionid(String accessToken, String unionid) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, unionid)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }

        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/user/getbyunionid", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        try {
            JSONObject formBody = new JSONObject().set("unionid", unionid);
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 根据userid获取用户详情
     * <a href="https://ding-doc.dingtalk.com/document/app/query-user-details">...</a>
     */
    public static ApiResult<JSONObject> getUserDetailByUserId(String accessToken, String userid) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, userid)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/get", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        try {
            JSONObject formBody = new JSONObject().set("userid", userid).set("language", "zh_CN");
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 自定义机器人消息发送
     * <a href="https://developers.dingtalk.com/document/robots/custom-robot-access">...</a>
     */
    public static ApiResult<JSONObject> sendRobotMsg(String accessToken, JSONObject formBody) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/robot/send", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson);
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 发送工作通知
     * <a href="https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages">...</a>
     */
    public static ApiResult<JSONObject> sendNotifyMsg(String accessToken, JSONObject formBody) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/message/corpconversation/asyncsend_v2", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson);
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 注册回调事件
     * <a href="https://developers.dingtalk.com/document/app/registers-event-callback-interfaces">...</a>
     */
    public static ApiResult<JSONObject> registerCallback(String accessToken, String aesKey, String token, String callbackUrl, String[] callbackTag) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, aesKey, token)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/call_back/register_call_back", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        JSONObject formBody = new JSONObject()
                .set("aes_key", aesKey)
                .set("token", token)
                .set("url", callbackUrl)
                .set("call_back_tag", callbackTag);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson);
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 上传媒体文件
     * <a href="https://developers.dingtalk.com/document/app/upload-media-files">...</a>
     */
    public static ApiResult<?> uploadMedia(String accessToken, String type, String filePath) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/media/upload", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .form("type", type)
                    .form("media", new FileSystemResource(filePath));
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * ASR 一句话语音识别
     * <a href="https://developers.dingtalk.com/document/app/asr-short-sentence-recognition">...</a>
     */
    public static ApiResult<?> asrVoiceTranslate(String accessToken, String mediaId) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, mediaId)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/asr/voice/translate", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        JSONObject formBody = new JSONObject().set("media_id", mediaId);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * OCR文字识别
     * <a href="https://developers.dingtalk.com/document/app/structured-image-recognition-api">...</a>
     */
    public static ApiResult<JSONObject> ocrStructuredRecognize(String accessToken, String type, String mediaUrl) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken, mediaUrl)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/ocr/structured/recognize", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        JSONObject formBody = new JSONObject()
                .set("type", type)
                .set("mediaUrl", mediaUrl);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 根据部门id获得子部门id数组
     */
    public static ApiResult<?> getDeptIdList(String accessToken, String deptId) {
        // 三元组结果
        ApiResult<List<JSONObject>> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/department/listsubid", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        JSONObject formBody = new JSONObject().set("dept_id", deptId);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(JSONUtil.getByPath(resultJson, "result.dept_id_list", new ArrayList<>()));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 根据部门id获得用户列表
     * <a href="https://open.dingtalk.com/document/orgapp/queries-the-complete-information-of-a-department-user">...</a>
     */
    public static ApiResult<JSONObject> getUserListByDeptId(String accessToken, JSONObject formBody) {
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        // token结果
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/list", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        try {
            HttpRequest request = HttpRequest
                    .post(url)
                    .body(formBody.toString());
            log.debug(request.toString());
            request.then(httpResponse -> {
                JSONObject resultJson = JSONUtil.parseObj(httpResponse.body());
                apiResult.setSuccess(resultJson.getInt("errcode") == 0)
                        .setCode(resultJson.getStr("errcode"))
                        .setMsg(resultJson.getStr("errmsg"))
                        .setData(resultJson.getJSONObject("result"));
            });
            return apiResult;
        } catch (Exception e) {
            return apiResult.error(ERROR_CODE_EXCEPTION, url + "=>exception=>" + e.getMessage());
        }
    }

    /**
     * 获得所有的部门id,钉钉sb接口不能一次性获取所有部门,也不支持级联获取。
     * 使用triple封装结果，避免异常的丢失，返回结果参考Result,分别是success、msg、deptIdList
     * <a href="https://open.dingtalk.com/document/orgapp/obtain-a-sub-department-id-list-v2">...</a>
     */
    public static ApiResult<List<Integer>> getAllDeptIdList(String accessToken) {
        ApiResult<List<Integer>> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        // token结果
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/department/listsubid", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
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
                    HttpRequest request = HttpRequest
                            .post(url)
                            .body(formBody.toString());
                    log.debug(request.toString());
                    request.then(httpResponse -> dept_sub_id_list.addAll(JSONUtil.getByPath(JSONUtil.parseObj(httpResponse.body()), "result.dept_id_list", new ArrayList<>())));
                } catch (Exception e) {
                    log.error(url + "=>exception=>", e);
                }
            });
            // 反馈给循环条件
            dept_id_list = dept_sub_id_list;
            // 塞入结果数组
            departmemtIdList.addAll(dept_sub_id_list);
        }
        return apiResult.success(departmemtIdList);
    }

    /**
     * 获得部门下所有用户列表，钉钉sb接口,用户挂在根部门上
     * 使用triple封装结果，避免异常的丢失
     * <a href="https://open.dingtalk.com/document/orgapp/queries-the-complete-information-of-a-department-user">...</a>
     */
    public static ApiResult<List<JSONObject>> getUserListByDeptIds(String accessToken, List<Integer> deptIds) {
        ApiResult<List<JSONObject>> apiResult = ApiResult.of();
        if (StrUtil.hasBlank(accessToken) || CollUtil.isEmpty(deptIds)) {
            return apiResult.error(ERROR_CODE_BAD_REQUEST, "参数不能为空");
        }
        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/list", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
        List<JSONObject> userList = new ArrayList<>();
        deptIds.forEach(deptId -> {
            AtomicInteger cursor = new AtomicInteger(0);
            while (cursor.get() >= 0) {
                try {
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
                    log.error(url + "=>exception=>", e);
                    cursor.set(-1);
                }
            }
        });
        return apiResult.success(CollUtil.distinct(userList, (Function<JSONObject, Object>) entries -> entries.getStr("userid"), true));
    }

}

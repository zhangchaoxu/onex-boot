package com.nb6868.onex.common.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.core.io.FileSystemResource;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 钉钉接口工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class DingTalkApiUtils {

    // token 缓存,有效时间2小时
    static TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7200 * 1000);

    private static final String ACS_TOKEN_KEY = "x-acs-dingtalk-access-token";
    private static final String BASE_URL = "https://oapi.dingtalk.com";

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
            String result = HttpUtil.post(url, new JSONObject().set("tmp_auth_code", code).toString());
            JSONObject resultJson = JSONUtil.parseObj(result);
            triple.setLeft(resultJson.getInt("errcode") == 0);
            triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
            triple.setRight(resultJson.getJSONObject("user_info"));
        } catch (Exception e) {
            triple.setMiddle("getuserinfo_bycode接口调用失败," + e.getMessage());
        }
        return triple;
    }

    /**
     * 获取企业内部应用的access_token
     * <a href="https://ding-doc.dingtalk.com/document/app/obtain-orgapp-token">...</a>
     */
    public static Triple<Boolean, String, String> getAccessToken(String appKey, String appSecret, boolean refresh) {
        // 三元组结果
        MutableTriple<Boolean, String, String> triple = new MutableTriple<>(false, null, null);
        String token = tokenCache.get(appKey, false);
        if (refresh || StrUtil.isBlank(token)) {
            // 强制刷新,或者缓存为空
            try {
                String url = HttpUtil.get(BASE_URL + "/gettoken", Dict.create().set("appkey", appKey).set("appsecret", appSecret));
                JSONObject resultJson = JSONUtil.parseObj(url);
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getStr("access_token"));
                if (StrUtil.isNotBlank(resultJson.getStr("access_token"))) {
                    tokenCache.put(appKey, resultJson.getStr("access_token"));
                }
            } catch (Exception e) {
                triple.setMiddle("获取token异常:" + e.getMessage());
            }
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
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/getuserinfo", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, new JSONObject().set("code", code)));
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("result"));
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("topapi/v2/user/getuserinfo接口调用失败," + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 获取用户access token
     */
    public static Triple<Boolean, String, JSONObject> getUserContactByCode(String accessKey, String appSecret, String code) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            String accessToken;
            try {
                String url = "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
                JSONObject requestBody = new JSONObject().set("clientId", accessKey).set("clientSecret", appSecret).set("code", code).set("grantType", "authorization_code");
                String result = HttpRequest.post(url)
                        .body(requestBody.toString())
                        .header(ACS_TOKEN_KEY, tokenResponse.getRight())
                        .execute()
                        .body();
                accessToken = JSONUtil.parseObj(result).getStr("accessToken");
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("oauth2/userAccessToken接口调用失败," + e.getMessage());
                return triple;
            }

            try {
                JSONObject resultJson2 = JSONUtil.parseObj(HttpRequest.get("https://api.dingtalk.com/v1.0/contact/users/" + "me")
                        .header(ACS_TOKEN_KEY, accessToken)
                        .execute()
                        .body());
                triple.setLeft(resultJson2.getInt("errcode") == 0);
                triple.setMiddle(resultJson2.getInt("errcode") + ":" + resultJson2.getStr("errmsg"));
                triple.setRight(resultJson2.getJSONObject("result"));
            } catch (Exception e2) {
                triple.setLeft(false);
                triple.setMiddle("contact/users接口调用失败," + e2.getMessage());
                return triple;
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
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
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/user/getbyunionid", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, new JSONObject().set("unionid", unionid).toString()));
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("result"));
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("topapi/user/getbyunionid接口调用失败," + e.getMessage());
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
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                //通讯录语言 zh_CN/en_US
                JSONObject requestBody = new JSONObject().set("userid", userid).set("language", "zh_CN");
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/get", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("result"));
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("topapi/v2/user/get接口调用失败," + e.getMessage());
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
    public static Triple<Boolean, String, JSONObject> sendRobotMsg(String accessToken, JSONObject requestBody) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, new JSONObject());
        try {
            String url = HttpUtil.urlWithForm(BASE_URL + "/robot/send", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            String result = HttpUtil.post(url, requestBody.toString());
            JSONObject resultJson = JSONUtil.parseObj(result);
            triple.setLeft(resultJson.getInt("errcode") == 0);
            triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
            triple.setRight(resultJson);
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
    public static Triple<Boolean, String, JSONObject> sendNotifyMsg(String accessKey, String appSecret, JSONObject requestBody) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/message/corpconversation/asyncsend_v2", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson);
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
    public static Triple<Boolean, String, JSONObject> registerCallback(String aesKey, String token, String callbackUrl, String[] callbackTag, String accessToken) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.set("aes_key", aesKey);
            requestBody.set("token", token);
            requestBody.set("url", callbackUrl);
            requestBody.set("call_back_tag", callbackTag);
            String url = HttpUtil.urlWithForm(BASE_URL + "/call_back/register_call_back", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
            if (resultJson.getInt("errcode") == 0) {
                triple.setLeft(true);
            } else {
                triple.setLeft(false);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
            }
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("call_back/register_call_back接口调用失败," + e.getMessage());
        }
        return triple;
    }

    /**
     * 上传媒体文件
     * <a href="https://developers.dingtalk.com/document/app/upload-media-files">...</a>
     */
    public static Triple<Boolean, String, JSONObject> uploadMedia(String type, String filePath, String accessToken) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", type);
            requestBody.put("media", new FileSystemResource(filePath));
            String url = HttpUtil.urlWithForm(BASE_URL + "/media/upload", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody));
            triple.setLeft(resultJson.getInt("errcode") == 0);
            triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
            triple.setRight(resultJson.getJSONObject("result"));
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("media.upload接口调用失败," + e.getMessage());
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
            JSONObject requestBody = new JSONObject();
            requestBody.set("media_id", mediaId);
            String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/asr/voice/translate", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
            triple.setLeft(resultJson.getInt("errcode") == 0);
            triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
            triple.setRight(resultJson.getJSONObject("result"));
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("topapi/asr/voice/translate接口调用失败," + e.getMessage());
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
            JSONObject requestBody = new JSONObject()
                    .set("type", type)
                    .set("mediaUrl", mediaUrl);
            String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/ocr/structured/recognize", Dict.create().set("access_token", accessToken), Charset.defaultCharset(), true);
            JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
            triple.setLeft(resultJson.getInt("errcode") == 0);
            triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
            triple.setRight(resultJson.getStr("result"));
        } catch (Exception e) {
            triple.setLeft(false);
            triple.setMiddle("topapi/asr/voice/translate接口调用失败," + e.getMessage());
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
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/department/listsubid", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url,  new JSONObject().set("dept_id", deptId).toString()));
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(JSONUtil.getByPath(resultJson, "result.dept_id_list", new ArrayList<>()));
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("topapi/v2/department/listsubid接口调用失败," + e.getMessage());
            }
        } else {
            triple.setMiddle(tokenResponse.getMiddle());
        }
        return triple;
    }

    /**
     * 根据部门id获得子部门id数组
     */
    public static Triple<Boolean, String, JSONObject> getUserListByDeptId(String accessKey, String appSecret, JSONObject requestBody) {
        // 三元组结果
        MutableTriple<Boolean, String, JSONObject> triple = new MutableTriple<>(false, null, null);
        // token结果
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            try {
                String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/list", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
                triple.setLeft(resultJson.getInt("errcode") == 0);
                triple.setMiddle(resultJson.getInt("errcode") + ":" + resultJson.getStr("errmsg"));
                triple.setRight(resultJson.getJSONObject("result"));
            } catch (Exception e) {
                triple.setLeft(false);
                triple.setMiddle("topapi/v2/department/listsubid接口调用失败," + e.getMessage());
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
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
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
                        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/department/listsubid", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                        JSONObject resultJson = JSONUtil.parseObj(HttpUtil.post(url, new JSONObject().set("dept_id", deptId).toString()));
                        dept_sub_id_list.addAll(JSONUtil.getByPath(resultJson, "result.dept_id_list", new ArrayList<>()));
                    } catch (Exception e) {
                        log.error("获取子部门列表失败", e);
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
        Triple<Boolean, String, String> tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.getLeft()) {
            int size = 10;
            List<JSONObject> userList = new ArrayList<>();
            deptIds.forEach(deptId -> {
                int cursor = 0;
                while (cursor >= 0) {
                    try {
                        String url = HttpUtil.urlWithForm(BASE_URL + "/topapi/v2/user/list", Dict.create().set("access_token", tokenResponse.getRight()), Charset.defaultCharset(), true);
                        JSONObject requestBody = new JSONObject()
                                .set("dept_id", deptId)
                                .set("cursor", cursor)
                                .set("size", size);
                        JSONObject result = JSONUtil.parseObj(HttpUtil.post(url, requestBody.toString()));
                        if (result.getInt("errcode", -1) == 0 && result.getJSONObject("result") != null) {
                            if (JSONUtil.getByPath(result, "result.has_more", false)) {
                                cursor = result.getJSONObject("result").getInt("next_cursor");
                            } else {
                                cursor = -1;
                            }
                            userList.addAll(result.getJSONObject("result").getBeanList("list", JSONObject.class));
                        } else {
                            cursor = -1;
                        }
                    } catch (Exception e) {
                        log.error("获得用户列表失败", e);
                        cursor = -1;
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

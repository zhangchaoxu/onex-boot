package com.nb6868.onex.common.dingtalk;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钉钉接口工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class DingTalkApi {

    // token 缓存,有效时间2小时
    static TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7200 * 1000);

    private final static String ACS_TOKEN_KEY = "x-acs-dingtalk-access-token";

    /**
     * 获取企业内部应用的access_token
     * https://ding-doc.dingtalk.com/document/app/obtain-orgapp-token
     */
    private final static String GET_TOKEN = "https://oapi.dingtalk.com/gettoken?appkey={1}&appsecret={2}";

    /**
     * 通过临时授权码获取授权用户的个人信息
     * https://ding-doc.dingtalk.com/document/app/obtain-the-user-information-based-on-the-sns-temporary-authorization
     */
    //private final static String GET_USERINFO_BY_CODE = "https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey={1}&timestamp={2}&signature={3}";
    private final static String GET_USERINFO_BY_CODE = "https://oapi.dingtalk.com/sns/getuserinfo_bycode";

    /**
     * 通过免登码获取用户信息
     * 1. https://developers.dingtalk.com/document/app/logon-free-process
     * 2. https://developers.dingtalk.com/document/app/obtain-the-userid-of-a-user-by-using-the-log-free
     */
    private final static String GET_USERINFO_V2_BY_CODE = "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo?access_token={1}";

    /**
     * 根据unionid获取用户userid
     * https://ding-doc.dingtalk.com/document/app/query-a-user-by-the-union-id
     */
    private final static String GET_USER_BY_UNIONID = "https://oapi.dingtalk.com/topapi/user/getbyunionid?access_token={1}";

    /**
     * 根据userid获取用户详情
     * https://ding-doc.dingtalk.com/document/app/query-user-details
     */
    private final static String GET_USER_DETAIL_BY_USERID = "https://oapi.dingtalk.com/topapi/v2/user/get?access_token={1}";

    /**
     * 发送工作通知
     * https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages
     */
    private final static String MESSAGE_SEND = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token={1}";

    /**
     * 获取用户access token
     */
    private final static String GET_USER_ACCESS_TOKEN = "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";

    /**
     * 获取用户信息
     */
    private final static String GET_USER_CONTACT = "https://api.dingtalk.com/v1.0/contact/users/";

    /**
     * 自定义机器人消息发送
     * https://developers.dingtalk.com/document/robots/custom-robot-access
     */
    private final static String ROBOT_SEND = "https://oapi.dingtalk.com/robot/send?access_token={1}";

    /**
     * 上传媒体文件
     * https://developers.dingtalk.com/document/app/upload-media-files
     */
    private final static String UPLOAD_MEDIA = "https://oapi.dingtalk.com/media/upload?access_token={1}";

    /**
     * ASR 一句话语音识别
     * https://developers.dingtalk.com/document/app/asr-short-sentence-recognition
     */
    private final static String ASR_VOICE_TRANSLATE = "https://oapi.dingtalk.com/topapi/asr/voice/translate?access_token={1}";

    /**
     * OCR文字识别
     * https://developers.dingtalk.com/document/app/structured-image-recognition-api
     */
    private final static String OCR_STRUCTURED_RECOGNIZE = "https://oapi.dingtalk.com/topapi/ocr/structured/recognize?access_token={1}";

    /**
     * 注册回调事件
     * https://developers.dingtalk.com/document/app/registers-event-callback-interfaces
     */
    private final static String REGISTER_CALLBACK = "https://oapi.dingtalk.com/call_back/register_call_back?access_token={1}";

    /**
     * 获取部门ID列表
     * https://open.dingtalk.com/document/isvapp/obtain-a-sub-department-id-list-v2
     */
    private final static String DEPARTMENT_ID_LIST = "https://oapi.dingtalk.com/topapi/v2/department/listsubid?access_token={1}";

    /**
     * 通过部门id获得用户详情列表
     * https://open.dingtalk.com/document/isvapp/queries-the-simple-information-of-a-department-user
     */
    private final static String USER_DETAIL_LIST = "https://oapi.dingtalk.com/topapi/v2/user/list?access_token={1}";


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
     */
    public static GetUserInfoByCodeResponse getUserInfoByCode(String appId, String appSecret, String code) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("tmp_auth_code", code);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = SignUtils.signToBase64(timestamp, appSecret, "HmacSHA256");
        // fuck RestTemplate 自动会对url做encode
        URI uri = UriComponentsBuilder
                .fromHttpUrl(GET_USERINFO_BY_CODE)
                .queryParam("accessKey", appId)
                .queryParam("timestamp", timestamp)
                .queryParam("signature", SignUtils.urlEncode(signature))
                .build(true)
                .toUri();

        try {
            return new RestTemplate().postForObject(uri, requestBody, GetUserInfoByCodeResponse.class);
        } catch (Exception e) {
            GetUserInfoByCodeResponse response = new GetUserInfoByCodeResponse();
            response.setErrcode(1000);
            response.setErrmsg("getuserinfo_bycode接口调用失败," + e.getMessage());
            return response;
        }
    }

    /**
     * 获取企业内部应用的access_token
     */
    public static AccessTokenResponse getAccessToken(String appKey, String appSecret, boolean refresh) {
        String token = tokenCache.get(appKey, false);
        if (refresh || StrUtil.isBlank(token)) {
            // 强制刷新,或者缓存为空
            try {
                AccessTokenResponse response = new RestTemplate().getForObject(GET_TOKEN, AccessTokenResponse.class, appKey, appSecret);
                if (response != null && response.isSuccess()) {
                    tokenCache.put(appKey, response.getAccess_token());
                }
                return response;
            } catch (Exception e) {
                AccessTokenResponse response = new AccessTokenResponse();
                response.setErrcode(1000);
                response.setErrmsg("gettoken接口调用失败," + e.getMessage());
                return response;
            }
        }
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token(token);
        accessTokenResponse.setExpires_in(0);
        return accessTokenResponse;
    }

    /**
     * 通过免登码code获取用户信息
     */
    public static ResultResponse<UserGetByCodeResponse> getUserInfoV2ByCode(String accessKey, String appSecret, String code) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("code", code);
            try {
                return new RestTemplate().exchange(GET_USERINFO_V2_BY_CODE, HttpMethod.POST,
                        new HttpEntity<>(form), new ParameterizedTypeReference<ResultResponse<UserGetByCodeResponse>>() {
                        }, tokenResponse.getAccess_token()).getBody();
            } catch (Exception e) {
                ResultResponse<UserGetByCodeResponse> response = new ResultResponse<>();
                response.setErrcode(1000);
                response.setErrmsg("topapi/v2/user/getuserinfo接口调用失败,," + e.getMessage());
                return response;
            }
        } else {
            ResultResponse<UserGetByCodeResponse> response = new ResultResponse<>();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 通过免登码code获取用户信息
     */
    public static ResultResponse<UserContactResponse> getUserContactByCode(String accessKey, String appSecret, String code) {
        // 1 获得系统access token
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            JSONObject param = new JSONObject().set("clientId", accessKey).set("clientSecret", appSecret).set("code", code).set("grantType", "authorization_code");
            HttpHeaders headers = new HttpHeaders();
            headers.add(ACS_TOKEN_KEY, tokenResponse.getAccess_token());
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONObject userAccessTokenResult;
            try {
                userAccessTokenResult = new RestTemplate().postForObject(GET_USER_ACCESS_TOKEN, new HttpEntity<>(param.toString(), headers), JSONObject.class);
            } catch (Exception e) {
                ResultResponse<UserContactResponse> response = new ResultResponse<>();
                response.setErrcode(1000);
                response.setErrmsg("获取用户AccessToken失败");
                return response;
            }

            if (StrUtil.isNotBlank(userAccessTokenResult.getStr("accessToken"))) {
                HttpHeaders headers2 = new HttpHeaders();
                headers2.add(ACS_TOKEN_KEY, userAccessTokenResult.getStr("accessToken"));
                headers2.setContentType(MediaType.APPLICATION_JSON);
                try {
                    ResponseEntity<UserContactResponse> userContact = new RestTemplate().exchange(GET_USER_CONTACT + "me", HttpMethod.GET, new HttpEntity<>(null, headers2), UserContactResponse.class);
                    ResultResponse<UserContactResponse> response = new ResultResponse<>();
                    response.setResult(userContact.getBody());
                    return response;
                } catch (Exception e) {
                    ResultResponse<UserContactResponse> response = new ResultResponse<>();
                    response.setErrcode(1000);
                    response.setErrmsg("获取用户具体信息失败");
                    return response;
                }
            } else {
                ResultResponse<UserContactResponse> response = new ResultResponse<>();
                response.setErrcode(1000);
                response.setErrmsg("获取用户AccessToken失败");
                return response;
            }
        } else {
            ResultResponse<UserContactResponse> response = new ResultResponse<>();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 根据unionid获取用户userid
     */
    public static GetUserIdByUnionidResponse getUserIdByUnionid(String accessKey, String appSecret, String unionid) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("unionid", unionid);
            try {
                return new RestTemplate().postForObject(GET_USER_BY_UNIONID, requestBody, GetUserIdByUnionidResponse.class, tokenResponse.getAccess_token());
            } catch (Exception e) {
                GetUserIdByUnionidResponse response = new GetUserIdByUnionidResponse();
                response.setErrcode(1000);
                response.setErrmsg("getbyunionid接口调用失败," + e.getMessage());
                return response;
            }
        } else {
            GetUserIdByUnionidResponse response = new GetUserIdByUnionidResponse();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 根据userid获取用户详情
     */
    public static GetUserDetailByUseridResponse getUserDetailByUserId(String accessKey, String appSecret, String userid) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("userid", userid);
            requestBody.put("language", "zh_CN");//  通讯录语言 zh_CN/en_US
            try {
                return new RestTemplate().postForObject(GET_USER_DETAIL_BY_USERID, requestBody, GetUserDetailByUseridResponse.class, tokenResponse.getAccess_token());
            } catch (Exception e) {
                GetUserDetailByUseridResponse response = new GetUserDetailByUseridResponse();
                response.setErrcode(1000);
                response.setErrmsg("topapi/v2/user/get接口调用失败,," + e.getMessage());
                return response;
            }
        } else {
            GetUserDetailByUseridResponse response = new GetUserDetailByUseridResponse();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 自定义机器人消息发送
     */
    public static BaseResponse sendRobotMsg(String accessToken, JSONObject requestBody) {
        try {
            return new RestTemplate().postForObject(ROBOT_SEND, requestBody, BaseResponse.class, accessToken);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setErrcode(1000);
            response.setErrmsg("robot/send接口调用失败," + e.getMessage());
            return response;
        }
    }

    /**
     * 消息通知发送
     */
    public static BaseResponse sendNotifyMsg(String accessKey, String appSecret, JSONObject requestBody) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            try {
                return new RestTemplate().postForObject(MESSAGE_SEND, requestBody, BaseResponse.class, tokenResponse.getAccess_token());
            } catch (Exception e) {
                BaseResponse response = new BaseResponse();
                response.setErrcode(1000);
                response.setErrmsg("notify/send接口调用失败," + e.getMessage());
                return response;
            }
        } else {
            BaseResponse response = new BaseResponse();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 注册回调地址
     */
    public static BaseResponse registerCallback(String aesKey, String token, String url, String[] callbackTag, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("aes_key", aesKey);
        requestBody.put("token", token);
        requestBody.put("url", url);
        requestBody.put("call_back_tag", callbackTag);
        try {
            return new RestTemplate().postForObject(REGISTER_CALLBACK, requestBody, BaseResponse.class, accessToken);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setErrcode(1000);
            response.setErrmsg("call_back/register_call_back接口调用失败," + e.getMessage());
            return response;
        }
    }

    /**
     * 上传媒体文件
     */
    public static UploadMediaResponse uploadMedia(String type, String filePath, String accessToken) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("type", type);
        form.add("media", new FileSystemResource(filePath));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);

        try {
            return new RestTemplate().postForObject(UPLOAD_MEDIA, requestEntity, UploadMediaResponse.class, accessToken);
        } catch (Exception e) {
            UploadMediaResponse response = new UploadMediaResponse();
            response.setErrcode(1000);
            response.setErrmsg("media.upload接口调用失败," + e.getMessage());
            return response;
        }
    }

    /**
     * ASR 一句话语音识别
     */
    public static String asrVoiceTranslate(String mediaId, String accessToken) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("media_id", mediaId);
        try {
            return new RestTemplate().exchange(ASR_VOICE_TRANSLATE, HttpMethod.POST,
                    new HttpEntity<>(form), new ParameterizedTypeReference<String>() {
                    }, accessToken).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * OCR文字识别
     */
    public static ResultResponse<String> ocrStructuredRecognize(String type, String mediaUrl, String accessToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("type", type);
        form.add("mediaUrl", mediaUrl);
        try {
            return new RestTemplate().exchange(OCR_STRUCTURED_RECOGNIZE, HttpMethod.POST,
                    new HttpEntity<>(form), new ParameterizedTypeReference<ResultResponse<String>>() {
                    }, accessToken).getBody();
        } catch (Exception e) {
            ResultResponse<String> response = new ResultResponse<>();
            response.setErrcode(1000);
            response.setErrmsg("ocr.structured.recognize接口调用失败," + e.getMessage());
            return response;
        }
    }

    /**
     * 根据部门id获得子部门id数组
     */
    public static DeptIdListResponse getDeptIdList(String accessKey, String appSecret, String deptId) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("dept_id", deptId);
            try {
                return new RestTemplate().postForObject(DEPARTMENT_ID_LIST, requestBody, DeptIdListResponse.class, tokenResponse.getAccess_token());
            } catch (Exception e) {
                DeptIdListResponse response = new DeptIdListResponse();
                response.setErrcode(1000);
                response.setErrmsg("topapi/v2/department/listsubid" + e.getMessage());
                return response;
            }
        } else {
            DeptIdListResponse response = new DeptIdListResponse();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 根据部门id获得子部门id数组
     */
    public static UserListResponse getUserListByDeptId(String accessKey, String appSecret, Map<String, String> params) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            try {
                return new RestTemplate().postForObject(USER_DETAIL_LIST, params, UserListResponse.class, tokenResponse.getAccess_token());
            } catch (Exception e) {
                UserListResponse response = new UserListResponse();
                response.setErrcode(1000);
                response.setErrmsg("topapi/v2/user/list" + e.getMessage());
                return response;
            }
        } else {
            UserListResponse response = new UserListResponse();
            response.setErrcode(tokenResponse.getErrcode());
            response.setErrmsg(tokenResponse.getErrmsg());
            return response;
        }
    }

    /**
     * 获得所有的部门id,会将所有异常都吞掉
     *
     * @param accessKey key
     * @param appSecret 密钥
     * @return 部门id数组
     */
    public static List<String> getAllDeptIdList(String accessKey, String appSecret) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            // 初始化的时候把根id放进去，用户可能会挂载根上
            List<String> departmemtIdList = CollUtil.newArrayList("1");
            // 根部门dept_id传1
            List<String> dept_id_list = CollUtil.newArrayList("1");
            // 逐级遍历
            while (!dept_id_list.isEmpty()) {
                // 初始化一个新的数组存储结果
                List<String> dept_sub_id_list = CollUtil.newArrayList();
                dept_id_list.forEach(deptId -> {
                    try {
                        JSONObject result = JSONUtil.parseObj(cn.hutool.http.HttpRequest.post("https://oapi.dingtalk.com/topapi/v2/department/listsubid?access_token=" + tokenResponse.getAccess_token())
                                .body(new JSONObject().set("dept_id", deptId).toString())
                                .execute().body());
                        if (result.getInt("errcode", -1) == 0 && result.getJSONObject("result") != null) {
                            dept_sub_id_list.addAll(result.getJSONObject("result").getBeanList("dept_id_list", String.class));
                        }
                    } catch (Exception e) {
                        log.error("获取子部门列表失败", e);
                    }
                });
                // 反馈给循环条件
                dept_id_list = dept_sub_id_list;
                // 塞入结果数组
                departmemtIdList.addAll(dept_sub_id_list);
            }
            return departmemtIdList;
        } else {
            log.error("获得token失败:{}", tokenResponse.getErrmsg());
            return CollUtil.newArrayList();
        }
    }

    /**
     * 获得部门下所有用户列表，会将异常吞掉
     */
    public static JSONArray getUserListByDeptIds(String accessKey, String appSecret, List<String> deptIds) {
        AccessTokenResponse tokenResponse = getAccessToken(accessKey, appSecret, false);
        if (tokenResponse.isSuccess()) {
            int size = 10;
            Map<String, JSONObject> userIdMap = new HashMap<>();
            deptIds.forEach(deptId -> {
                int cursor = 0;
                while (cursor >= 0) {
                    try {
                        JSONObject result = JSONUtil.parseObj(HttpRequest.post("https://oapi.dingtalk.com/topapi/v2/user/list?access_token=" + tokenResponse.getAccess_token())
                                .body(new JSONObject()
                                        .set("dept_id", deptId)
                                        .set("cursor", cursor)
                                        .set("size", size)
                                        .toString())
                                .execute().body());
                        if (result.getInt("errcode", -1) == 0 && result.getJSONObject("result") != null) {
                            if (result.getJSONObject("result").getBool("has_more")) {
                                cursor = result.getJSONObject("result").getInt("next_cursor");
                            } else {
                                cursor = -1;
                            }
                            for (int i = 0; i < result.getJSONObject("result").getJSONArray("list").size(); i++) {
                                JSONObject user = result.getJSONObject("result").getJSONArray("list").getJSONObject(i);
                                userIdMap.put(user.getStr("userid"), user);
                            }
                        } else {
                            cursor = -1;
                        }
                    } catch (Exception e) {
                        log.error("获得用户列表失败", e);
                        cursor = -1;
                    }
                }
            });
            JSONArray userList = new JSONArray();
            userIdMap.forEach((s, entries) -> userList.add(entries));
            return userList;
        } else {
            return new JSONArray();
        }
    }

}

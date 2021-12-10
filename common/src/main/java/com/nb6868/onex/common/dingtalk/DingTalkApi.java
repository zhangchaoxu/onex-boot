package com.nb6868.onex.common.dingtalk;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.util.AliSignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉接口工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DingTalkApi {

    // token 缓存,有效时间2小时
    static TimedCache<String, String> tokenCache = CacheUtil.newTimedCache(7200 * 1000);

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
     * 通过临时授权码获取授权用户的个人信息
     */
    public static GetUserInfoByCodeResponse getUserInfoByCode(String appId, String appSecret, String code) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("tmp_auth_code", code);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = AliSignUtils.signature(timestamp, appSecret, "HmacSHA256");
        // fuck RestTemplate 自动会对url做encode
        URI uri = UriComponentsBuilder
                .fromHttpUrl(GET_USERINFO_BY_CODE)
                .queryParam("accessKey", appId)
                .queryParam("timestamp", timestamp)
                .queryParam("signature", AliSignUtils.urlEncode(signature))
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
                return new RestTemplate().getForObject(GET_TOKEN, AccessTokenResponse.class, appKey, appSecret);
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
    public static BaseResponse sendRobotMsg(String accessToken, Dict requestBody) {
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

}

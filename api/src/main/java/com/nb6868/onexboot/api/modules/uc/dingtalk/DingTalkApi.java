package com.nb6868.onexboot.api.modules.uc.dingtalk;

import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.util.AliSignUtils;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉接口工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DingTalkApi {

    @Autowired
    static Cache dingtalkTokenCache;

    /**
     * 获取企业内部应用的access_token
     * https://ding-doc.dingtalk.com/document/app/obtain-orgapp-token
     */
    private final static String GET_TOKEN = "https://oapi.dingtalk.com/gettoken?appkey={1}&appsecret={2}";

    /**
     * 通过临时授权码获取授权用户的个人信息
     * https://ding-doc.dingtalk.com/document/app/obtain-the-user-information-based-on-the-sns-temporary-authorization
     */
    private final static String GET_USERINFO_BY_CODE = "https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey={1}&timestamp={2}&signature={3}";

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
     * https://ding-doc.dingtalk.com/document/app/custom-robot-access
     */
    private final static String ROBOT_SEND = "https://oapi.dingtalk.com/robot/send?access_token={1}&timestamp={2}&sign={3}";

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
     * 通过临时授权码获取授权用户的个人信息
     */
    public static GetUserInfoByCodeResponse getUserInfoByCode(String accessKey, String appSecret, String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("tmp_auth_code", code);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = AliSignUtils.signature(timestamp, appSecret, "HmacSHA256");
        return restTemplate.postForObject(GET_USERINFO_BY_CODE, requestBody, GetUserInfoByCodeResponse.class, accessKey, timestamp, signature);
    }

    /**
     * 通过临时授权码获取授权用户的个人信息
     */
    public static AccessTokenResponse getAccessToken(String appKey, String appSecret) {
        return new RestTemplate().getForObject(GET_TOKEN, AccessTokenResponse.class, appKey, appSecret);
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

        return new RestTemplate().postForObject(UPLOAD_MEDIA, requestEntity, UploadMediaResponse.class, accessToken);
    }

    /**
     * ASR 一句话语音识别
     */
    public static String asrVoiceTranslate(String mediaId, String accessToken) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("media_id", mediaId);
        return new RestTemplate().exchange(ASR_VOICE_TRANSLATE, HttpMethod.POST,
                new HttpEntity<>(form), new ParameterizedTypeReference<String>() {}, accessToken).getBody();
    }

    /**
     * 根据unionid获取用户userid
     */
    public static GetUserIdByUnionidResponse getUserByUnionid(String unionid) {
        //String token = dingtalkTokenCache.get("accessToken", String.class);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("unionid", unionid);
        return restTemplate.postForObject(GET_USER_BY_UNIONID, requestBody, GetUserIdByUnionidResponse.class, "accessToken");
    }

    /**
     * 根据unionid获取用户userid
     */
    public static GetUserDetailByUseridResponse getUserDetailByUserId(String userid) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userid", userid);
        requestBody.put("language", "zh_CN");//  通讯录语言 zh_CN/en_US
        return restTemplate.postForObject(GET_USER_DETAIL_BY_USERID, requestBody, GetUserDetailByUseridResponse.class, "accessToken");
    }

    /**
     * 自定义机器人消息发送
     */
    public static BaseResponse sendRobotMsg(String accessToken, String signKey, Kv requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = AliSignUtils.signature(timestamp + "\n" + signKey, signKey, "HmacSHA256");
        return restTemplate.postForObject(ROBOT_SEND, requestBody, GetUserDetailByUseridResponse.class, accessToken, timestamp, signature);
    }

}

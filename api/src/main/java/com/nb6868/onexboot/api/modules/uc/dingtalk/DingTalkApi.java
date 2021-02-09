package com.nb6868.onexboot.api.modules.uc.dingtalk;

import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.util.AliSignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
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
    Cache dingtalkTokenCache;

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
     * 机器人消息发送
     * https://ding-doc.dingtalk.com/document/app/custom-robot-access
     */
    private final static String ROBOT_SEND = "https://oapi.dingtalk.com/robot/send?access_token={1}&timestamp={2}&sign={3}";

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
    public static AccessTokenResponse getAccessToken(String appkey, String appSecret) {
        return new RestTemplate().getForObject(GET_TOKEN, AccessTokenResponse.class, appkey, appSecret);
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
     * 根据unionid获取用户userid
     */
    public static BaseResponse sendRobotMsg(String accessToken, String signKey, Kv requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = AliSignUtils.signature(timestamp + "\n" + signKey, signKey, "HmacSHA256");
        return restTemplate.postForObject(ROBOT_SEND, requestBody, GetUserDetailByUseridResponse.class, accessToken, timestamp, signature);
    }

}

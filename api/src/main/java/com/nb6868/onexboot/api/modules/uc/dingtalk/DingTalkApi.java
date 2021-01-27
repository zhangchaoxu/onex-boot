package com.nb6868.onexboot.api.modules.uc.dingtalk;

import com.nb6868.onexboot.common.util.AliSignUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉接口工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DingTalkApi {

    private final static String GET_BY_CODE = "https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey={1}&timestamp={2}&signature={3}";

    /**
     * 钉钉扫码授权登录，通过code登录
     * see https://ding-doc.dingtalk.com/document/app/scan-qr-code-to-log-on-to-third-party-websites
     */
    public static GetUserInfoByCodeResponse getUserInfoByCode(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("tmp_auth_code", code);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = AliSignUtils.signature(timestamp, "", "HmacSHA256");
        GetUserInfoByCodeResponse result = restTemplate.postForObject(GET_BY_CODE, requestBody, GetUserInfoByCodeResponse.class,
                "", timestamp, signature);
        return result;
    }

}

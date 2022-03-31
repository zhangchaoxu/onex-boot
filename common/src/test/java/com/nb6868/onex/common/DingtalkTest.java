package com.nb6868.onex.common;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.dingtalk.AccessTokenResponse;
import com.nb6868.onex.common.dingtalk.BaseResponse;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.dingtalk.GetUserInfoByCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("钉钉")
public class DingtalkTest {

    @Test
    @DisplayName("获得accessToken")
    void getAccessToken() {
        AccessTokenResponse response = DingTalkApi.getAccessToken("dingzy0fp2ltwvg3gw8m", "edraUxgxNGlNB1mD8TINHRrPTDzu-unRoVAoAszfLpc7IKQUML7mFLbVbhsGIf1y", true);
        System.out.println(response);
    }

    @Test
    @DisplayName("获得用户信息")
    void getUserInfoByCode() {
        GetUserInfoByCodeResponse response = DingTalkApi.getUserInfoByCode("dingoadm6szm8j6izamqdy", "TXq0of-I8c5IXw9sd1cZgwWQOyc5p9hvF8T7T7joiBduEtfeHvWXxmis631MxwD_", "1234");
        log.error(response.getErrcode() + ":" + response.getErrmsg());
    }

    @Test
    @DisplayName("获得用户信息")
    void sendRobotMsg() {
        JSONObject dict = new JSONObject();
        // 实际只有txt可以@
        /*dict.set("msgtype", "markdown")
                .set("at", Dict.create().set("atMobiles", new String[]{""}))
                .set("markdown", Dict.create()
                        .set("title", "*Robot*我是标题")
                        .set("text", "#### 我是具体消息\n[详见](https://"));*/
        dict.set("msgtype", "text")
                .set("text", Dict.create().set("content", "*Robot*提醒\nssssss"))
                .set("at", Dict.create().set("atMobiles", new String[]{""}));
        BaseResponse response = DingTalkApi.sendRobotMsg("", dict);
        System.out.println(response);
    }


}

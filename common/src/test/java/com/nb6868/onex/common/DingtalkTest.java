package com.nb6868.onex.common;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.util.DingTalkApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@DisplayName("钉钉")
public class DingtalkTest {

    @Test
    @DisplayName("获得accessToken")
    void getAccessToken() {
        ApiResult<String> response = DingTalkApi.getOauth2AccessToken("", "-", true);
        ApiResult<String> response2 = DingTalkApi.getAccessToken("", "-", true);
        log.error(response.toString());
        log.error(response2.toString());
    }

    @Test
    @DisplayName("获得子部门id")
    void getDepartmentIdList() {
        ApiResult<List<Integer>> response = DingTalkApi.getAllDeptIdList("195a1443e9d737a6baa3af5114dc9df0");
        log.error(response.toString());
    }

    @Test
    @DisplayName("获得用户信息")
    void getUserInfoByCode() {
        ApiResult<JSONObject> response = DingTalkApi.getUserInfoByCode("", "", "1234");
        log.error(response.toString());
    }

    @Test
    @DisplayName("发送消息")
    void sendRobotMsg() {
        JSONObject dict = new JSONObject();
        // 实际只有txt可以@
        /*dict.set("msgtype", "markdown")
                .set("at", Dict.create().set("atMobiles", new String[]{""}))
                .set("markdown", Dict.create()
                        .set("title", "*Robot*我是标题")
                        .set("text", "#### 我是具体消息\n[详见](https://"));*/
        dict.set("msgtype", "markdown")
                .set("at", new JSONObject().set("atMobiles", new ArrayList<>()))
                .set("markdown", new JSONObject().set("title", "任务[苏州-]已完成").set("text", "nihaoma a"));
        ApiResult<JSONObject> response = DingTalkApi.sendRobotMsg("", dict);
        log.error(response.toString());
    }


}

package com.nb6868.onex.common;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.util.DingTalkApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@Slf4j
@DisplayName("钉钉")
public class DingtalkTest {

    @Test
    @DisplayName("获得accessToken")
    void getAccessToken() {
        Triple response = DingTalkApiUtils.getAccessToken("", "-", true);
        log.error(response.toString());
    }

    @Test
    @DisplayName("获得子部门id")
    void getDepartmentIdList() {
        Triple response = DingTalkApiUtils.getAllDeptIdList("", "-");
        log.error(response.toString());
    }

    @Test
    @DisplayName("获得用户信息")
    void getUserInfoByCode() {
        Triple response = DingTalkApiUtils.getUserInfoByCode("", "", "1234");
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
        Triple response = DingTalkApiUtils.sendRobotMsg("", dict);
        log.error(response.toString());
    }


}

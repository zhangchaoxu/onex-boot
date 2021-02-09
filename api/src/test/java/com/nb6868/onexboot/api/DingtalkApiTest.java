package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.modules.uc.dingtalk.BaseResponse;
import com.nb6868.onexboot.api.modules.uc.dingtalk.DingTalkApi;
import com.nb6868.onexboot.common.pojo.Kv;
import org.junit.jupiter.api.Test;

/**
 * 钉钉接口测试方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DingtalkApiTest {

    @Test
    void sendRobotMsg() {
        Kv kv = Kv.init();
        kv.set("msgtype", "text")
                .set("text", Kv.init().set("content", "测试消息内容"))
                .set("at", Kv.init().set("atMobiles", new String[]{"13012345678"}));
        BaseResponse response = DingTalkApi.sendRobotMsg("", "", kv);
        System.out.println(response);
    }
}

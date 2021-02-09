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
                .set("at", Kv.init().set("atMobiles", new String[]{"13252421988"}));
        BaseResponse response = DingTalkApi.sendRobotMsg("88a97f990d209ea485dd8361e08aa2e00028cd49334e9007a303ab95f87635a7",
                "SEC0bd907295fb0683a82a2b17d93d73b647eba50386cd5164013b905dbb44ff4ff", kv);
        System.out.println(response);
    }
}

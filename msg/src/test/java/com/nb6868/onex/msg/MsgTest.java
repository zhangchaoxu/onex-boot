package com.nb6868.onex.msg;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles({"common", "test"})
@Slf4j
@DisplayName("消息")
public class MsgTest {

    /*@Autowired
    MsgService msgService;

    @Test()
    @DisplayName("消息发送")
    void sendWxMpTemplateMsg() {
        MsgSendReq form = new MsgSendReq();
        form.setTplCode("WX_TEMPLATE_NOTIFY");
        form.setMailTo("");
        JSONObject content = new JSONObject().set("thing1", "thing1").set("character_string9","ABC1234").set("thing3", "thing3");
        form.setContentParams(content);
        MsgTplBody mailTpl = msgService.getTplByCode(null, form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");

        // 结果标记
        boolean flag = msgService.sendMail(form);
        if (flag) {
            log.error("短信发送成功");
        } else {
            log.error("短信发送失败");
        }
    }*/

    @Test
    @DisplayName("发送阿里云消息")
    void sendSmsAliyun() {
        // 封装阿里云接口参数
        Map<String, Object> paras = new HashMap<>();
    }

}

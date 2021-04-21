package com.nb6868.onexboot.api.modules.uc.controller;

import com.nb6868.onexboot.api.modules.uc.dingtalk.DingCallbackCrypto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 钉钉
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("dingtalk")
@Validated
@Api(tags = "钉钉")
@Slf4j
public class DingtalkController {

    /**
     * https://developers.dingtalk.com/document/app/configure-event-subcription
     */
    @PostMapping("eventCallback")
    @ApiOperation("事件订阅回调")
    public Map<String, String> eventCallback(@RequestParam(required = false) String msg_signature,
                                             @RequestParam(required = false) String timestamp,
                                             @RequestParam(required = false) String nonce,
                                             @RequestBody(required = false) Map<String, String> json) {
        try {
            // 1. 从http请求中获取加解密参数
            // 2. 使用加解密类型
            // Constant.OWNER_KEY 说明：
            // 1、开发者后台配置的订阅事件为应用级事件推送，此时OWNER_KEY为应用的APP_KEY。
            // 2、调用订阅事件接口订阅的事件为企业级事件推送，
            //      此时OWNER_KEY为：企业的appkey（企业内部应用）或SUITE_KEY（三方应用）
            DingCallbackCrypto callbackCrypto = new DingCallbackCrypto("", "", "");
            String encryptMsg = json.get("encrypt");
            String decryptMsg = callbackCrypto.getDecryptMsg(msg_signature, timestamp, nonce, encryptMsg);
            // 3. 反序列化回调事件json数据
            log.info(decryptMsg);
            /*JSONObject eventJson = JSON.parseObject(decryptMsg);
            String eventType = eventJson.getString("EventType");

            // 4. 根据EventType分类处理
            if ("check_url".equals(eventType)) {
                // 测试回调url的正确性
                log.info("测试回调url的正确性");
            } else if ("user_add_org".equals(eventType)) {
                // 处理通讯录用户增加事件
                log.info("发生了：" + eventType + "事件");
            } else {
                // 添加其他已注册的
                log.info("发生了：" + eventType + "事件");
            }*/
            // 5. 返回success的加密数据
            Map<String, String> successMap = callbackCrypto.getEncryptedMap("success");
            return successMap;
        } catch (DingCallbackCrypto.DingTalkEncryptException e) {
            e.printStackTrace();
            return null;
        }

    }

}

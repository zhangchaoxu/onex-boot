package com.nb6868.onex.api;

import cn.hutool.core.lang.Dict;
import com.nb6868.onex.api.modules.uc.dingtalk.*;
import org.junit.jupiter.api.Test;

/**
 * 钉钉接口测试方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DingtalkApiTest {

    @Test
    void sendRobotMsg() {
        Dict dict = Dict.create();
        dict.set("msgtype", "text")
                .set("text", Dict.create().set("content", "测试消息内容"))
                .set("at", Dict.create().set("atMobiles", new String[]{"13012345678"}));
        BaseResponse response = DingTalkApi.sendRobotMsg("", "", dict);
        System.out.println(response);
    }

    @Test
    void getAccessToken() {
        AccessTokenResponse response = DingTalkApi.getAccessToken("dingzy0fp2ltwvg3gw8m", "edraUxgxNGlNB1mD8TINHRrPTDzu-unRoVAoAszfLpc7IKQUML7mFLbVbhsGIf1y");
        System.out.println(response);
    }

    @Test
    void uploadMedia() {
        UploadMediaResponse response = DingTalkApi.uploadMedia("voice", "C://1.amr", "80878ad8026d39c8a647fd66391a86f9");
        System.out.println(response);
    }

    @Test
    void asrVoiceTranslate() {
        String response = DingTalkApi.asrVoiceTranslate("@@lATPDf0iyGhtg8DODAIAcM4u0LOS", "9ef940ba747536d5a9c568c177983101");
        System.out.println(response);
    }

    @Test
    void registerCallback() {
        BaseResponse response = DingTalkApi.registerCallback("EvnXTdTV2UmbtTTMBYoiENqZcGkioxhKhPiW5hXTCCR",
                "mLSiVo1RNFEK7tr5Xx8lApSLrWyUFpNfcJ1aBwEQoU1VouSGIe6iZi",
                "https://charles-dev.cn.utools.club/onex-boot-api/dingtalk/eventCallback",
                new String[]{"voice_translate_short_speech"},
                "9ef940ba747536d5a9c568c177983101");
        System.out.println(response);
    }

    @Test
    void ocrStructuredRecognize() {
        ResultResponse<String> response = DingTalkApi.ocrStructuredRecognize("blicense",
                "mLSiVo1RNFEK7tr5Xx8lApSLrWyUFpNfcJ1aBwEQoU1VouSGIe6iZi",
                "9ef940ba747536d5a9c568c177983101");
        System.out.println(response);
    }

}

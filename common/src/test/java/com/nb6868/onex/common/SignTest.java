package com.nb6868.onex.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@DisplayName("签名测试")
@Slf4j
public class SignTest {

    @Test
    @DisplayName("接口签名")
    void signApi() {
        // 加密字符串
        String secret = "abcd1234";
        // 接口参数
        Map<String, Object> apiParams = new HashMap<>();
        apiParams.put("outerId", "190948777859");
        apiParams.put("presHash", "presHash");
        apiParams.put("presTitle", "拼多多-190948777859");
        apiParams.put("presType", "199");
        apiParams.put("presEntityType", "1");
        apiParams.put("presEntityCode", "91330");
        apiParams.put("appKey", "190948777859");
        String apiParamSplit = ""; // "&"
        String apiParamJoin = ""; // "="
        // 接口Body
        Object apiBody = new JSONObject().set("h", 1);
        // 接口方法
        String apiMethod = "get";
        // 接口路径
        String apiPath = "/v1/devices";

        // 1. 参数拼接排序后拼接
        StrJoiner paramJoin = new StrJoiner(apiParamJoin);
        MapUtil.sort(apiParams).forEach((key, value) -> {
            // 部分字段不加入签名
            if (!"sign".equalsIgnoreCase(key)) {
                paramJoin.append(key + apiParamSplit + value);
            }
        });
        log.info("参数拼接,字符串={}", paramJoin);
        log.info("参数拼接+body,字符串={}", paramJoin + apiBody.toString());
        log.info("参数拼接+body+method,字符串={}", paramJoin + apiBody.toString() + apiMethod.toUpperCase());
        log.info("参数拼接+body+method+url,字符串={}", paramJoin + apiBody.toString() + apiMethod.toUpperCase() + apiPath);
        log.info("##############");
        String md5 = SecureUtil.md5(secret + paramJoin + apiBody.toString() + apiMethod.toUpperCase() + apiPath + secret);
        log.info("最终结果32位16进制md5,sign={}", md5);
    }

}

package com.nb6868.onex.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@DisplayName("签名测试")
@Slf4j
public class SignTest {

    @Test
    @DisplayName("接口签名")
    void signApi() {
        // 加密字符串
        String secret = "znrlZErG74WKWv6VLLbKFmUQ93VebesE";
        // 接口参数
        Map<String, Object> apiParams = new HashMap<>();
        apiParams.put("_t", System.currentTimeMillis());
        apiParams.put("clientkey", "K3OWcL9PBqzavTdrmlUDmID0FTnbUvdN");
        String apiParamSplit = ""; // "&"
        String apiParamJoin = ""; // "="
        // 接口Body
        Object apiBody = "";
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
        String paramJoin2 = SignUtils.paramToQueryString(apiParams, "", "");
        log.info("参数拼接,字符串={}", paramJoin);
        log.info("参数拼接,字符串2={}", paramJoin2);
        log.info("参数拼接+body,字符串={}", paramJoin + apiBody.toString());
        log.info("参数拼接+body+method,字符串={}", paramJoin + apiBody.toString() + apiMethod.toUpperCase());
        log.info("参数拼接+body+method+url,字符串={}", paramJoin + apiBody.toString() + apiMethod.toUpperCase() + apiPath);
        String rawString = secret + paramJoin + apiBody + apiMethod.toUpperCase() + apiPath + secret;
        log.info("加密前明文,字符串={}", rawString);
        log.info("##############");
        String md5Hutool = SecureUtil.md5(rawString);
        String md5Spring = DigestUtils.md5DigestAsHex(rawString.getBytes());
        // 3b9f4c773c7818b81d793ed4830401ab
        log.info("hutool最终结果32位16进制md5,sign={}", md5Hutool);
        log.info("java最终结果32位16进制md5,sign={}", md5Spring);
        log.info("https://api.zillionsource.com/v1/devices?clientkey={}&_t={}&sign={}", MapUtil.getStr(apiParams, "clientkey"), MapUtil.getStr(apiParams,"_t"), md5Hutool);
    }

}

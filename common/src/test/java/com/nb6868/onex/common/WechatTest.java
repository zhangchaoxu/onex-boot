package com.nb6868.onex.common;

import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.util.WechatApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("微信测试")
public class WechatTest {

    @DisplayName("获得accessToken")
    @Test
    void getAccessToken() {
        ApiResult<String> response = WechatApi.getStableAccessToken("client_credential", "", "", false);
        log.error(response.toString());
    }

}

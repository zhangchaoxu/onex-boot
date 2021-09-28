package com.nb6868.onex.common;

import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IP测试")
@Slf4j
public class IpTest {

    @Test
    @DisplayName("正则匹配ip")
    void matchIp() {
        String content = "10.0.0.99";
        boolean isMatch = ReUtil.isMatch("10.0.0.(\\d{1,3})", content);
        boolean isMatch2 = ReUtil.isMatch("10.0.0.199", content);
        log.error("isMatch={}", isMatch);
        log.error("isMatch2={}", isMatch2);
    }

}

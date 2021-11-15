package com.nb6868.onex.common;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IP测试")
@Slf4j
public class StringTest {

    @Test
    @DisplayName("string fmt")
    void matchIp() {
        String result = StrUtil.format("{}{}SSS", "111", StrUtil.nullToEmpty(null));
        log.error("result={}", result);
    }

}

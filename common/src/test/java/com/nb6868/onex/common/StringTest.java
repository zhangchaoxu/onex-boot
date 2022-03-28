package com.nb6868.onex.common;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

@DisplayName("IP测试")
@Slf4j
public class StringTest {

    @Test
    @DisplayName("string fmt")
    void matchIp() {
        String result = StrUtil.format("{}{}SSS", "111", StrUtil.nullToEmpty(null));
        log.error("result={}", result);
    }

    @Test
    @DisplayName("split")
    void split() {
        String result = null;
        StrUtil.splitTrim(result, ',').forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                log.error("result={}", s);
            }
        });
    }



}

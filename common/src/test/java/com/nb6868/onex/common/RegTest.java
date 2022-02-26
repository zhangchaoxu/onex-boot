package com.nb6868.onex.common;

import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("正则测试")
@Slf4j
public class RegTest {

    @Test
    @DisplayName("测试")
    void test() {
        // 加密字符串
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$";
        log.error("1={}", ReUtil.isMatch(regex, "znrlZErG74WKWv6VLLbKFmUQ93VebesE"));
        log.error("1={}", ReUtil.isMatch(regex, "zas!@#2"));
        log.error("1={}", ReUtil.isMatch(regex, "1121asdsa"));
        log.error("1={}", ReUtil.isMatch(regex, "!@1121asdsa!!"));
        log.error("1={}", ReUtil.isMatch(regex, "!@11223232312sfadsfs张撒打算打算的安顺1asdsa!!"));
    }
}

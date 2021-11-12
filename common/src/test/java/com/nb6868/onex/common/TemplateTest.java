package com.nb6868.onex.common;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import com.nb6868.onex.common.util.TemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("模板测试")
@Slf4j
public class TemplateTest {

    @Test
    @DisplayName("渲染文本模板")
    void renderRaw() {
        String raw = "您好${username}，请查收您的验证码";
        Dict param = Dict.create().set("username", "1111");
        String content = TemplateUtils.renderRaw(raw, param);
        log.error(content);
    }

}

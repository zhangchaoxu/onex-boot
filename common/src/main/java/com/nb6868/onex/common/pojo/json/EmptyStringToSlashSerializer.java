package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 空字符串转成斜杠
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class EmptyStringToSlashSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StrUtil.isBlank(value)) {
            gen.writeString("/");
        } else {
            gen.writeString(value);
        }
    }
}

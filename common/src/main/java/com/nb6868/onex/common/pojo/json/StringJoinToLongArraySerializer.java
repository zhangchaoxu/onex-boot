package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.List;

/**
 * 符号分割的string转long数组
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class StringJoinToLongArraySerializer extends JsonSerializer<List<Long>> {

    @Override
    public void serialize(List<Long> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeString("");
        } else {
            gen.writeString(StrUtil.join(",", value));
        }
    }

}

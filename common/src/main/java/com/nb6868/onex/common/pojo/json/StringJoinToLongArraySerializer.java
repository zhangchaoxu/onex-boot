package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.util.StrUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.List;

/**
 * 符号分割的string转long数组
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class StringJoinToLongArraySerializer extends ValueSerializer<List<Long>> {

    @Override
    public void serialize(List<Long> value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null || value.isEmpty()) {
            gen.writeString("");
        } else {
            gen.writeString(StrUtil.join(",", value));
        }
    }
}

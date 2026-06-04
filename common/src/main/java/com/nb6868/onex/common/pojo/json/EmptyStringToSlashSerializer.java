package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.util.StrUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;


/**
 * 空字符串转成斜杠
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class EmptyStringToSlashSerializer extends ValueSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (StrUtil.isBlank(value)) {
            gen.writeString("/");
        } else {
            gen.writeString(value);
        }
    }

}

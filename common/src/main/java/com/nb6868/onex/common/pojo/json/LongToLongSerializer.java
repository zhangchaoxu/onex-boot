package com.nb6868.onex.common.pojo.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * long转long
 * 默认会将long转string,但是有些地方需要long格式,所以单独处理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LongToLongSerializer extends ValueSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNumber(0);
        } else {
            gen.writeNumber(value);
        }
    }

}

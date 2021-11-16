package com.nb6868.onex.common.pojo.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * long转long
 * 默认会将long转string,但是有些地方需要long格式,所以单独处理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class LongToLongSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNumber(0);
        } else {
            gen.writeNumber(value);
        }
    }

}

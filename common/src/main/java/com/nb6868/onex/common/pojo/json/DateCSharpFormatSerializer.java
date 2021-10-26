package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * C#日期序列化
 * /Date(-62135596800000)/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DateCSharpFormatSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeString("/Date(-62135596800000)/");
        } else {
            gen.writeString("/Date(" + DateUtil.millisecond(value) + ")/");
        }
    }
}

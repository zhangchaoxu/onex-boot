package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.date.DateUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Date;

/**
 * C#日期序列化
 * /Date(-62135596800000)/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DateCSharpFormatSerializer extends ValueSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeString("/Date(-62135596800000)/");
        } else {
            gen.writeString("/Date(" + DateUtil.millisecond(value) + ")/");
        }
    }

}

package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.date.DateUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.Date;

/**
 * C# 格式 日期反序列化
 * /Date(-62135596800000)/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DateCSharpFormatDeserializer extends ValueDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws JacksonException {
        String txt = jsonParser.getString();
        if ("/Date(-62135596800000)/".equalsIgnoreCase(txt)) {
            // -62135596800000是WCF或MVC webservice返回的空日期
            return null;
        } else {
            return DateUtil.date(Long.parseLong(txt.replaceAll("/Date\\(", "").replaceAll("\\)/", "")));
        }
    }

}

package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * C# 格式 日期反序列化
 * /Date(-62135596800000)/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DateCSharpFormatDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String txt = jsonParser.getText();
        if ("/Date(-62135596800000)/".equalsIgnoreCase(txt)) {
            // -62135596800000是WCF或MVC webservice返回的空日期
            return null;
        } else {
            return DateUtil.date(Long.parseLong(txt.replaceAll("/Date\\(", "").replaceAll("\\)/", "")));
        }
    }
}

package com.nb6868.onex.common.pojo.json;

import cn.hutool.http.HtmlUtil;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * 带xss过滤的反序列化
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class XssStringDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctx)  {
        String txt = jsonParser.getString();
        return HtmlUtil.filter(txt);
    }

}

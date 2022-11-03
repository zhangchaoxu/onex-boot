package com.nb6868.onex.common.pojo.json;

import cn.hutool.http.HtmlUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 带xss过滤的反序列化
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class XssStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        String txt = jsonParser.getText();
        return HtmlUtil.filter(txt);
    }

}

package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * json数组转换成符号分割的string
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ArrayToStringJoinDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        // 判断是否数组
        if (node.isArray()) {
            if (node.isEmpty()) {
                return null;
            } else {
                return StrUtil.join(",", node);
            }
        } else {
            return node.asText();
        }
    }

}

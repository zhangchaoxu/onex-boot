package com.nb6868.onex.common.pojo.json;

import cn.hutool.core.util.StrUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

/**
 * json数组转换成符号分割的string
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ArrayToStringJoinDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws JacksonException {
        JsonNode node = jsonParser.readValueAsTree();
        // 判断是否数组
        if (node.isArray()) {
            if (node.isEmpty()) {
                return null;
            } else {
                return StrUtil.join(",", node);
            }
        } else {
            return node.asString();
        }
    }

}

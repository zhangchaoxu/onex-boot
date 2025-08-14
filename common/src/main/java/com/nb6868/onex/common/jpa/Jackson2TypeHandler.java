package com.nb6868.onex.common.jpa;

import cn.hutool.json.JSONNull;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Jackson 实现 JSON 字段类型处理器
 * 重写以实现hutool json中JSONNull的转换
 * 具体见 [自定义类型处理器](https://baomidou.com/guides/type-handler/)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class Jackson2TypeHandler extends JacksonTypeHandler {

    public Jackson2TypeHandler(Class<?> type) {
        super(type);
        setObjectMapper(initObjectMapper());
    }

    public Jackson2TypeHandler(Class<?> type, Field field) {
        super(type, field);
        setObjectMapper(initObjectMapper());
    }

    /**
     * 处理jackson解析处理问题
     */
    public static ObjectMapper initObjectMapper() {
        ObjectMapper OBJECT_MAPPER = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(JSONNull.class, new JsonSerializer<>() {
            @Override
            public void serialize(JSONNull jsonNull, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeNull();
            }
        });
        simpleModule.addDeserializer(JSONNull.class, new JsonDeserializer<>() {
            @Override
            public JSONNull deserialize(JsonParser jsonParser, DeserializationContext deserializationContext){
                return null;
            }
        });
        OBJECT_MAPPER.registerModule(simpleModule);
        return OBJECT_MAPPER;
    }

}

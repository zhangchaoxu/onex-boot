package com.nb6868.onex.common.util;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

/**
 * JSON 工具类。
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class JacksonUtils {

    /**
     * 私有构造函数。
     */
    private JacksonUtils() {
    }

    /**
     * Map type
     */
    public static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    /**
     * 普通对象 Mapper
     */
    private static JsonMapper.Builder mapperBuilder;

    public static JsonMapper.Builder getMapperBuilder() {
        if (JacksonUtils.mapperBuilder != null) {
            return JacksonUtils.mapperBuilder;
        }
        synchronized (JacksonUtils.class) {
            if (JacksonUtils.mapperBuilder != null) {
                return JacksonUtils.mapperBuilder;
            }
            JacksonUtils.mapperBuilder = JsonMapper.builder();
            // 设置忽略属性
            JacksonUtils.mapperBuilder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 设置允许转义字符,比如CTRL-CHAR
            JacksonUtils.mapperBuilder.configure(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true);
            // 设置时间格式
            JacksonUtils.mapperBuilder.defaultDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));
            JacksonUtils.mapperBuilder.defaultTimeZone(TimeZone.getTimeZone("GMT+8"));
            // Long类型转String类型，解决js中Long型数据精度丢失的问题 {https://mybatis.plus/guide/faq.html#id-worker-生成主键太长导致-js-精度丢失}
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            JacksonUtils.mapperBuilder.addModule(simpleModule);
            // 设置
            JacksonUtils.mapperBuilder.disable(MapperFeature.USE_ANNOTATIONS);
            return JacksonUtils.mapperBuilder;
        }
    }

    /**
     * 对象实例转JSON字符串。
     *
     * @param pojo 对象实例
     * @param <T>  对象类型
     * @return 转换的JSON字符串
     */
    public static <T> String pojoToJson(T pojo) {
        if (pojo == null) {
            return null;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().writeValueAsString(pojo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert Object2JSONString. ", e);
        }
    }

    /**
     * 对象实例转JSON字符串。
     *
     * @param pojo 对象实例
     * @param <T>  对象类型
     * @return 转换的JSON字符串
     */
    public static <T> String pojoToJson(T pojo, String defaultVal) {
        if (pojo == null) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().writeValueAsString(pojo);
        } catch (IOException e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * JSON字符串转对象实例。
     *
     * @param json      JSON串
     * @param pojoClass 对象类型
     * @param <T>       对象类型
     * @return 转换的对象实例
     */
    public static <T> T jsonToPojo(byte[] json, Class<T> pojoClass) {
        return jsonToPojo(json, pojoClass, null);
    }

    /**
     * JSON字符串转对象实例。
     *
     * @param json       JSON串
     * @param pojoClass  对象类型
     * @param <T>        对象类型
     * @param defaultVal 默认值
     * @return 转换的对象实例
     */
    public static <T> T jsonToPojo(byte[] json, Class<T> pojoClass, T defaultVal) {
        if (ObjectUtils.isEmpty(json)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().readValue(json, pojoClass);
        } catch (Exception e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * JSON字符串转对象实例。
     *
     * @param json      JSON串
     * @param pojoClass 对象类型
     * @param <T>       对象类型
     * @return 转换的对象实例
     */
    public static <T> T jsonToPojo(String json, Class<T> pojoClass) {
        return jsonToPojo(json, pojoClass, null);
    }

    /**
     * JSON字符串转对象实例。
     *
     * @param json       JSON串
     * @param pojoClass  对象类型
     * @param <T>        对象类型
     * @param defaultVal 默认值
     * @return 转换的对象实例
     */
    public static <T> T jsonToPojo(String json, Class<T> pojoClass, T defaultVal) {
        if (ObjectUtils.isEmpty(json)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().readValue(json, pojoClass);
        } catch (Exception e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * JSON字符串转对象实例。
     *
     * @param json      JSON串
     * @param pojoClass 对象类型
     * @param <T>       对象类型
     * @return 转换的对象实例
     */
    public static <T> T nodeToPojo(JsonNode json, Class<T> pojoClass) {
        return nodeToPojo(json, pojoClass, null);
    }

    /**
     * JSON字符串转对象实例。
     *
     * @param json       JSON串
     * @param pojoClass  对象类型
     * @param <T>        对象类型
     * @param defaultVal 默认值
     * @return 转换的对象实例
     */
    public static <T> T nodeToPojo(JsonNode json, Class<T> pojoClass, T defaultVal) {
        if (ObjectUtils.isEmpty(json)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().convertValue(json, pojoClass);
        } catch (Exception e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * JSON字符串转Map。
     *
     * @param json JSON串
     * @return 转换的Map实例
     */
    public static Map<String, Object> jsonToMap(String json) {
        return jsonToMap(json, null);
    }

    /**
     * JSON字符串转Map。
     *
     * @param json       JSON串
     * @param defaultVal 默认值
     * @return 转换的Map实例
     */
    public static Map<String, Object> jsonToMap(String json, Map<String, Object> defaultVal) {
        if (ObjectUtils.isEmpty(json)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().readValue(json, JacksonUtils.MAP_TYPE);
        } catch (Exception e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * Map转JSON字符串
     *
     * @return 转换的Map实例
     */
    public static String mapToJson(Map<String, Object> map) {
        return mapToJson(map, null);
    }

    /**
     * Map转JSON字符串
     *
     * @return 转换的Map实例
     */
    public static String mapToJson(Map<String, Object> map, String defaultVal) {
        if (ObjectUtils.isEmpty(map)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().writeValueAsString(map);
        } catch (final IOException e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * Map转Pojo
     *
     * @return 转换的Map实例
     */
    public static <T> T mapToPojo(Map<String, Object> map, Class<T> pojoClass, T defaultVal) {
        if (ObjectUtils.isEmpty(map)) {
            return defaultVal;
        }
        return jsonToPojo(pojoToJson(map), pojoClass, defaultVal);
    }

    /**
     * Map转Pojo
     *
     * @return 转换的Map实例
     */
    public static <T> T mapToPojo(Map<String, Object> map, Class<T> pojoClass) {
        return mapToPojo(map, pojoClass, null);
    }

    /**
     * JSON字符串转Node。
     *
     * @param json       JSON串
     * @return 转换的Node实例
     */
    public static JsonNode jsonToNode(String json) {
        if (ObjectUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().readTree(json);
        } catch (final IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * JSON字符串转Nide。
     *
     * @param json       JSON串
     * @return 转换的Node实例
     */
    public static <T> T jsonToPojoByTypeReference(String json, TypeReference<T> typeReference) {
        return jsonToPojoByTypeReference(json, typeReference, null);
    }

    /**
     * JSON字符串转Nide。
     *
     * @param json       JSON串
     * @return 转换的Node实例
     */
    public static <T> T jsonToPojoByTypeReference(String json, TypeReference<T> typeReference, T defaultValue) {
        if (ObjectUtils.isEmpty(json)) {
            return defaultValue;
        }
        try {
            return JacksonUtils.getMapperBuilder().build().readValue(json, typeReference);
        } catch (final IOException e) {
            log.error(e.getMessage());
            return defaultValue;
        }
    }

    /**
     * 合并两个json,注意source会覆盖target中同名字段
     */
    public static Map<String, Object> combineJson(String source, String target) {
        if (ObjectUtils.isEmpty(source)) {
            return jsonToMap(target, new HashMap<>(0));
        } else if (ObjectUtils.isEmpty(target)) {
            return jsonToMap(source, new HashMap<>(0));
        } else {
            Map<String, Object> targetMap = jsonToMap(target, new HashMap<>());
            Map<String, Object> sourceMap = jsonToMap(source, new HashMap<>());
            targetMap.putAll(sourceMap);
            return targetMap;
        }
    }

    /**
     * 合并两个json,注意source会覆盖target中同名字段
     */
    public static <T> T combineJsonToPojo(String source, String target, Class<T> pojoClass) {
        if (ObjectUtils.isEmpty(source)) {
            return jsonToPojo(target, pojoClass);
        } else if (ObjectUtils.isEmpty(target)) {
            return jsonToPojo(source, pojoClass);
        } else {
            Map<String, Object> map = combineJson(source, target);
            String json = pojoToJson(map);
            return jsonToPojo(json, pojoClass);
        }
    }

    // 参考AbstractJsonParser
    public static <T> T tryParse(Callable<T> parser) {
        return tryParse(parser, JsonParseException.class);
    }

    public static <T> T tryParse(Callable<T> parser, Class<? extends Exception> check) {
        try {
            return parser.call();
        } catch (Exception ex) {
            if (check.isAssignableFrom(ex.getClass())) {
                throw new JsonParseException(ex);
            }
            throw new IllegalStateException(ex);
        }
    }

}

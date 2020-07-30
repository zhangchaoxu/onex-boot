package com.nb6868.onex.booster.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * JSON 工具类。
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Slf4j
public class JacksonUtils {

    /**
     * Map type
     */
    public static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };
    /**
     * 普通对象 Mapper
     */
    private static ObjectMapper mapper;

    /**
     * 私有构造函数。
     */
    private JacksonUtils() {
    }

    /**
     * 获取 ObjectMapper 实例。
     *
     * @return ObjectMapper实例
     */
    private static ObjectMapper getMapper() {
        if (JacksonUtils.mapper != null) {
            return JacksonUtils.mapper;
        }
        synchronized (JacksonUtils.class) {
            if (JacksonUtils.mapper != null) {
                return JacksonUtils.mapper;
            }
            JacksonUtils.mapper = new ObjectMapper();
            // 设置时间格式
            JacksonUtils.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JacksonUtils.mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_TIME_PATTERN));
            JacksonUtils.mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            // 设置
            JacksonUtils.mapper.disable(MapperFeature.USE_ANNOTATIONS);
            return JacksonUtils.mapper;
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
            return JacksonUtils.getMapper().writeValueAsString(pojo);
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
            return JacksonUtils.getMapper().writeValueAsString(pojo);
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
        if (StringUtils.isEmpty(json)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapper().readValue(json, pojoClass);
        } catch (final IOException e) {
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
        if (StringUtils.isEmpty(json)) {
            return defaultVal;
        }
        try {
            return JacksonUtils.getMapper().readValue(json, JacksonUtils.MAP_TYPE);
        } catch (final IOException e) {
            log.error(e.getMessage());
            return defaultVal;
        }
    }

    /**
     * JSON字符串转Nide。
     *
     * @param json       JSON串
     * @return 转换的Node实例
     */
    public static JsonNode jsonToNode(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return JacksonUtils.getMapper().readTree(json);
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
        if (StringUtils.isEmpty(json)) {
            return defaultValue;
        }
        try {
            return JacksonUtils.getMapper().readValue(json, typeReference);
        } catch (final IOException e) {
            log.error(e.getMessage());
            return defaultValue;
        }
    }


}

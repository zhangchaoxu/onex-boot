package com.nb6868.onex.common.config;

import cn.hutool.core.map.MapUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态表名参数传递辅助类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class DynamicTableParamHelper {

    /**
     * 请求参数存取
     */
    private static final ThreadLocal<Map<String, Object>> PARAM_DATA = new ThreadLocal<>();

    /**
     * 设置单个请求参数
     */
    public static void setParamDataSingle(String key, Object value) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put(key, value);
        PARAM_DATA.set(requestData);
    }

    /**
     * 设置请求参数
     *
     * @param requestData 请求参数 MAP 对象
     */
    public static void setParamData(Map<String, Object> requestData) {
        PARAM_DATA.set(requestData);
    }

    /**
     * 清空请求参数
     */
    public static void removeParamData() {
        PARAM_DATA.remove();
    }

    /**
     * 获取请求参数
     *
     * @return 请求参数 MAP 对象
     */
    public static Map<String, Object> getParamData() {
        return PARAM_DATA.get();
    }

    /**
     * 获取请求参数
     *
     * @param key  请求参数
     * @param type 数据类型
     * @return 请求参数 MAP 对象
     */
    public static <T> T getParamData(String key, Class<T> type) {
        Map<String, Object> dataMap = getParamData();
        return MapUtil.get(dataMap, key, type);
    }

    /**
     * 获取请求参数
     *
     * @param key          请求参数
     * @param type         数据类型
     * @param defaultValue 默认值
     * @return 请求参数 MAP 对象
     */
    public static <T> T getParamData(String key, Class<T> type, T defaultValue) {
        Map<String, Object> dataMap = getParamData();
        return MapUtil.get(dataMap, key, type, defaultValue);
    }

    /**
     * 获取string格式请求参数
     */
    public static String getParamDataString(String key, String defaultValue) {
        return getParamData(key, String.class, defaultValue);
    }

}

package com.nb6868.onex.common.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.Map;

/**
 * 动态表名参数传递辅助类
 */
public class DynamicTableParamHelper {

    /**
     * 请求参数存取
     */
    private static final ThreadLocal<Map<String, Object>> PARAM_DATA = new ThreadLocal<>();

    /**
     * 设置请求参数
     *
     * @param requestData 请求参数 MAP 对象
     */
    public static void setParamData(Map<String, Object> requestData) {
        PARAM_DATA.set(requestData);
    }

    /**
     * 获取请求参数
     *
     * @param param 请求参数
     * @return 请求参数 MAP 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getParamData(String param) {
        Map<String, Object> dataMap = getParamData();
        if (CollectionUtils.isNotEmpty(dataMap)) {
            return (T) dataMap.get(param);
        }
        return null;
    }

    /**
     * 获取请求参数
     *
     * @return 请求参数 MAP 对象
     */
    public static Map<String, Object> getParamData() {
        return PARAM_DATA.get();
    }

}

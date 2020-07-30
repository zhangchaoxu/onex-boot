package com.nb6868.onex.booster.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 参数工具
 *
 * @author Charles
 */
public class ParamUtils {

    public static boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        } else {
            return object.toString().trim().length() == 0;
        }
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isEmpty(Map<String, Object> params, String key) {
        if (null == params || StringUtils.isBlank(key)) {
            return true;
        } else {
            return isEmpty(params.get(key));
        }
    }

    public static boolean isNotEmpty(Map<String, Object> params, String key) {
        return !isEmpty(params, key);
    }

    /**
     * 转换为long值
     *
     * @param object     参数
     * @param defaultVal 默认值
     * @return
     */
    public static boolean toBoolean(Object object, boolean defaultVal) {
        if (isNotEmpty(object)) {
            try {
                String obj = object.toString();
                defaultVal = "true".equalsIgnoreCase(obj) || "1".equalsIgnoreCase(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultVal;
    }

    /**
     * 转换为long值
     *
     * @param object     参数
     * @param defaultVal 默认值
     * @return
     */
    public static long toLong(Object object, long defaultVal) {
        if (isNotEmpty(object)) {
            try {
                defaultVal = Long.parseLong(object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultVal;
    }

    /**
     * 转换为int值
     *
     * @param object     参数
     * @param defaultVal 默认值
     * @return
     */
    public static int toInt(Object object, int defaultVal) {
        if (isNotEmpty(object)) {
            try {
                defaultVal = Integer.parseInt(object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultVal;
    }

    public static String[] toArray(Map<String, Object> params, String key) {
        return toArray(params, key, ",");
    }

    public static String[] toArray(Map<String, Object> params, String key, String regex) {
        if (isNotEmpty(params, key)) {
            return params.get(key).toString().split(regex);
        }
        return new String[]{};
    }

}

package com.nb6868.onex.booster.pojo;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * 链式map
 *
 * @author Chill
 */
@SuppressWarnings("unchecked")
public class Kv extends LinkedCaseInsensitiveMap<Object> {

    private Kv() {

    }

    /**
     * 创建Kv
     *
     * @return Kv
     */
    public static Kv init() {
        return new Kv();
    }

    public static HashMap newMap() {
        return new HashMap(16);
    }

    /**
     * 设置列
     *
     * @param attr  属性
     * @param value 值
     * @return 本身
     */
    public Kv set(String attr, Object value) {
        this.put(attr, value);
        return this;
    }

    /**
     * 设置列，当键或值为null时忽略
     *
     * @param attr  属性
     * @param value 值
     * @return 本身
     */
    public Kv setIgnoreNull(String attr, Object value) {
        if (null != attr && null != value) {
            set(attr, value);
        }
        return this;
    }

    public Object getObj(String key) {
        return super.get(key);
    }

    /**
     * 获得特定类型值
     *
     * @param <T>          值类型
     * @param attr         字段名
     * @param defaultValue 默认值
     * @return 字段值
     */
    public <T> T get(String attr, T defaultValue) {
        final Object result = get(attr);
        return (T) (result != null ? result : defaultValue);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public String getStr(String attr) {
        Object str = get(attr);
        if (null == str) {
            return null;
        }
        return String.valueOf(str);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Integer getInt(String attr) {
        String str = getStr(attr);
        if (str == null) {
            return -1;
        }
        try {
            return Integer.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return -1;
        }
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Long getLong(String attr) {
        String str = getStr(attr);
        if (str == null) {
            return -1L;
        }
        try {
            return Long.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return -1L;
        }
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Float getFloat(String attr) {
        String str = getStr(attr);
        if (str == null) {
            return -1.0f;
        }
        try {
            return Float.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return -1.0f;
        }
    }

    public Double getDouble(String attr) {
        String str = getStr(attr);
        if (str == null) {
            return -1.00;
        }
        try {
            return Double.valueOf(str);
        } catch (final NumberFormatException nfe) {
            return -1.00;
        }
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Boolean getBool(String attr) {
        Object value = get(attr);
        if (value != null) {
            String val = String.valueOf(value);
            val = val.toLowerCase().trim();
            return Boolean.parseBoolean(val);
        }
        return null;
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public byte[] getBytes(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Date getDate(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Time getTime(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Timestamp getTimestamp(String attr) {
        return get(attr, null);
    }

    /**
     * 获得特定类型值
     *
     * @param attr 字段名
     * @return 字段值
     */
    public Number getNumber(String attr) {
        return get(attr, null);
    }

    @Override
    public Kv clone() {
        return (Kv) super.clone();
    }

}

package com.nb6868.onex.booster.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Wrapper查询工具
 * 一来是方便使用
 * 二来在xx(condition, object)的时候，即使condition=false，object中的方法也会执行会带来错误
 *
 * @param <T>
 * @author Charles zhangchaoxu@gmail.com
 */
public class WrapperUtils<T> {

    private QueryWrapper<T> wrapper;
    private Map<String, Object> params;

    public WrapperUtils(QueryWrapper<T> wrapper, Map<String, Object> params) {
        this.wrapper = wrapper;
        this.params = params;
    }

    public QueryWrapper<T> getQueryWrapper() {
        return wrapper;
    }

    /**
     * key是否存在
     *
     * @param key
     * @return
     */
    private boolean isKeyEmpty(String key) {
        if (null == params || StringUtils.isBlank(key)) {
            return true;
        } else {
            return params.get(key) == null || params.get(key).toString().trim().length() == 0;
        }
    }

    private boolean isKeyNotEmpty(String key) {
        return !isKeyEmpty(key);
    }

    public WrapperUtils<T> in(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.in(column, Arrays.asList(params.get(key).toString().split(",")));
        }
        return this;
    }

    public WrapperUtils<T> in(String key, String column, String splitRegex) {
        if (isKeyNotEmpty(key)) {
            wrapper.in(column, Arrays.asList(params.get(key).toString().split(splitRegex)));
        }
        return this;
    }

    public WrapperUtils<T> like(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.like(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> likeLeft(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.likeLeft(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> likeRight(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.likeRight(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> notLike(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.notLike(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> eq(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.eq(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> ne(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.ne(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> ge(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.ge(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> gt(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.gt(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> le(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.le(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> lt(String key, String column) {
        if (isKeyNotEmpty(key)) {
            wrapper.lt(column, params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> apply(String key) {
        if (isKeyNotEmpty(key)) {
            wrapper.apply(params.get(key).toString());
        }
        return this;
    }

    public WrapperUtils<T> last(String key, String lastSql) {
        if (isKeyNotEmpty(key)) {
            wrapper.last(lastSql);
        }
        return this;
    }

    /**
     * mysql only
     *
     * @param key
     * @return
     */
    public WrapperUtils<T> limit(String key) {
        if (isKeyNotEmpty(key)) {
            wrapper.last("limit " + params.get(key));
        }
        return this;
    }

    public WrapperUtils<T> limit(Integer limit) {
        if (limit > 0) {
            wrapper.last("limit " + limit);
        }
        return this;
    }

    /**
     * 查询条件封装
     * see {com.baomidou.mybatisplus.core.conditions.interfaces.Nested}
     * @param key key
     * @param consumer consumer
     * @return
     */
    public WrapperUtils<T> and(String key, Consumer<QueryWrapper<T>> consumer) {
        if (isKeyNotEmpty(key)) {
            wrapper.and(consumer);
        }
        return this;
    }

    public WrapperUtils<T> or(String key, Consumer<QueryWrapper<T>> consumer) {
        if (isKeyNotEmpty(key)) {
            wrapper.or(consumer);
        }
        return this;
    }

    public WrapperUtils<T> nested(String key, Consumer<QueryWrapper<T>> consumer) {
        if (isKeyNotEmpty(key)) {
            wrapper.nested(consumer);
        }
        return this;
    }

}

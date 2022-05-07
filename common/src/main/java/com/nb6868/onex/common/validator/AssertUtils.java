package com.nb6868.onex.common.validator;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.nb6868.onex.common.util.MessageUtils;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import org.springframework.util.ObjectUtils;

import java.util.function.Supplier;


/**
 * 校验工具类
 * 参考 cn.hutool.lang.Assert
 *
 * 注意：区别于Assert，是符合规则才抛出异常
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class AssertUtils {

    /**
     * 是否false
     */
    public static void isFalse(boolean expression) {
        isFalse(expression, ErrorCode.ERROR_REQUEST, MessageUtils.getMessage(ErrorCode.ERROR_REQUEST));
    }

    public static void isFalse(boolean expression, Integer code) {
        isFalse(expression, code, MessageUtils.getMessage(code));
    }

    public static void isFalse(boolean expression, String msg) {
        isFalse(expression, ErrorCode.ERROR_REQUEST, msg);
    }

    public static void isFalse(boolean expression, Integer code, String msg) {
        Assert.isTrue(expression, ()-> new OnexException(code, msg));
    }

    /**
     * 是否true
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, ErrorCode.ERROR_REQUEST, MessageUtils.getMessage(ErrorCode.ERROR_REQUEST));
    }

    public static void isTrue(boolean expression, Integer code) {
        isTrue(expression, code, MessageUtils.getMessage(code));
    }

    public static void isTrue(boolean expression, String msg) {
        isTrue(expression, ErrorCode.ERROR_REQUEST, msg);
    }

    public static void isTrue(boolean expression, Integer code, String msg) {
        Assert.isFalse(expression, ()-> new OnexException(code, msg));
    }

    /**
     * 是否是null值
     */
    public static void isNull(Object object) {
        isNull(object, ErrorCode.ERROR_REQUEST, MessageUtils.getMessage(ErrorCode.ERROR_REQUEST));
    }

    public static void isNull(Object object, Integer code) {
        isNull(object, code, MessageUtils.getMessage(code));
    }

    public static void isNull(Object object, String msg) {
        isNull(object, ErrorCode.ERROR_REQUEST, msg);
    }

    public static void isNull(Object object, Integer code, String msg) {
        Assert.notNull(object, ()-> new OnexException(code, msg));
    }

    /**
     * 是否为空
     */
    public static void isEmpty(Object object) {
        isEmpty(object, ErrorCode.ERROR_REQUEST, MessageUtils.getMessage(ErrorCode.ERROR_REQUEST));
    }

    public static void isEmpty(Object object, Integer code) {
        isEmpty(object, code, MessageUtils.getMessage(code));
    }

    public static void isEmpty(Object object, String msg) {
        isEmpty(object, ErrorCode.ERROR_REQUEST, msg);
    }

    public static void isEmpty(Object object, Integer code, String msg) {
        if (ObjectUtil.isEmpty(object)) {
            throw new OnexException(code, msg);
        }
    }
}

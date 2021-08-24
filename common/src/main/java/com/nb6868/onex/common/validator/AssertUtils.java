package com.nb6868.onex.common.validator;

import com.nb6868.onex.common.util.MessageUtils;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import org.springframework.util.ObjectUtils;


/**
 * 校验工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class AssertUtils {

    /**
     * 是否false
     */
    public static void isFalse(boolean object) {
        isFalse(object, ErrorCode.ERROR_REQUEST, MessageUtils.getMessage(ErrorCode.ERROR_REQUEST));
    }

    public static void isFalse(boolean object, Integer code) {
        isFalse(object, code, MessageUtils.getMessage(code));
    }

    public static void isFalse(boolean object, String msg) {
        isFalse(object, ErrorCode.ERROR_REQUEST, msg);
    }

    public static void isFalse(boolean object, Integer code, String msg) {
        if (!object) {
            throw new OnexException(code, msg);
        }
    }

    /**
     * 是否true
     */
    public static void isTrue(boolean object) {
        isTrue(object, ErrorCode.ERROR_REQUEST, MessageUtils.getMessage(ErrorCode.ERROR_REQUEST));
    }

    public static void isTrue(boolean object, Integer code) {
        isTrue(object, code, MessageUtils.getMessage(code));
    }

    public static void isTrue(boolean object, String msg) {
        isTrue(object, ErrorCode.ERROR_REQUEST, msg);
    }

    public static void isTrue(boolean object, Integer code, String msg) {
        if (object) {
            throw new OnexException(code, msg);
        }
    }

    /**
     * 是否null
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
        if (object == null) {
            throw new OnexException(code, msg);
        }
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
        if (ObjectUtils.isEmpty(object)) {
            throw new OnexException(code, msg);
        }
    }
}

package com.nb6868.onexboot.common.validator;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.util.MessageUtils;
import org.springframework.util.ObjectUtils;


/**
 * 校验工具类
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public class AssertUtils {

    /**
     * 是否false
     */
    public static void isFalse(boolean object) {
        isFalse(object, ErrorCode.INTERNAL_SERVER_ERROR, MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public static void isFalse(boolean object, Integer code) {
        isFalse(object, code, MessageUtils.getMessage(code));
    }

    public static void isFalse(boolean object, String msg) {
        isFalse(object, ErrorCode.INTERNAL_SERVER_ERROR, msg);
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
        isTrue(object, ErrorCode.INTERNAL_SERVER_ERROR, MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public static void isTrue(boolean object, Integer code) {
        isTrue(object, code, MessageUtils.getMessage(code));
    }

    public static void isTrue(boolean object, String msg) {
        isTrue(object, ErrorCode.INTERNAL_SERVER_ERROR, msg);
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
        isNull(object, ErrorCode.INTERNAL_SERVER_ERROR, MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public static void isNull(Object object, Integer code) {
        isNull(object, code, MessageUtils.getMessage(code));
    }

    public static void isNull(Object object, String msg) {
        isNull(object, ErrorCode.INTERNAL_SERVER_ERROR, msg);
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
        isEmpty(object, ErrorCode.INTERNAL_SERVER_ERROR, MessageUtils.getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public static void isEmpty(Object object, Integer code) {
        isEmpty(object, code, MessageUtils.getMessage(code));
    }

    public static void isEmpty(Object object, String msg) {
        isEmpty(object, ErrorCode.INTERNAL_SERVER_ERROR, msg);
    }

    public static void isEmpty(Object object, Integer code, String msg) {
        if (ObjectUtils.isEmpty(object)) {
            throw new OnexException(code, msg);
        }
    }
}

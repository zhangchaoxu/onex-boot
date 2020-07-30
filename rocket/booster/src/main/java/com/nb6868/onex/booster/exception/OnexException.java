package com.nb6868.onex.booster.exception;

import com.nb6868.onex.booster.util.MessageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OnexException extends RuntimeException {

    private int code;
    private String msg;

    public OnexException(int code) {
        super(MessageUtils.getMessage(code));
        this.code = code;
        this.msg = getMessage();
    }

    public OnexException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public OnexException(int code, Throwable e) {
        super(MessageUtils.getMessage(code), e);
        this.code = code;
        this.msg = getMessage();
    }

    public OnexException(int code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
        this.msg = msg;
    }

    public OnexException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public OnexException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return code + ":" + msg;
    }
}

package com.nb6868.onex.booster.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 消息获取工具
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public class MessageUtils {

    private final static MessageSource messageSource;

    static {
        messageSource = (MessageSource) SpringContextUtils.getBean("messageSource");
    }

    public static String getMessage(int code) {
        return getMessage(code, new String[0]);
    }

    public static String getMessage(int code, String... params) {
        return messageSource.getMessage(String.valueOf(code), params, LocaleContextHolder.getLocale());
    }
}

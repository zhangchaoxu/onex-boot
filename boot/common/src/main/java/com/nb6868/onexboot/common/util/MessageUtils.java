package com.nb6868.onexboot.common.util;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

import java.nio.charset.StandardCharsets;

/**
 * 消息获取工具
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
public class MessageUtils {

    private final static ReloadableResourceBundleMessageSource messageSource;
    private final static MessageSourceResourceBundleLocator messageSourceSourceBundleLocator;

    /**
     * 文件路径在yml配置文件中定义
     */
    static {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setCacheSeconds(-1);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setBasenames("i18n/messages");

        //
        messageSourceSourceBundleLocator = new MessageSourceResourceBundleLocator(messageSource);
    }

    public static MessageSourceResourceBundleLocator getMessageSourceSourceBundleLocator() {
        return messageSourceSourceBundleLocator;
    }

    public static ReloadableResourceBundleMessageSource getMessageSource() {
        return messageSource;
    }

    public static String getMessage(int code) {
        return getMessage(code, new String[0]);
    }

    public static String getMessage(int code, String... params) {
        return messageSource.getMessage(String.valueOf(code), params, LocaleContextHolder.getLocale());
    }
}
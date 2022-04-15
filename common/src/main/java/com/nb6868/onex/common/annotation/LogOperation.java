package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {

    // 日志记录
    String value() default "";

    // 日志类型
    String type() default "operation";

    // 存储类型, db/logger
    String storeType() default "db";

    // 存储范围 默认所有 success只保存成功,error只保存失败
    String scope() default "";

}

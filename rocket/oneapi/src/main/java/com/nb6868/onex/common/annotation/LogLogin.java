package com.nb6868.onex.common.annotation;

import com.nb6868.onex.modules.uc.UcConst;

import java.lang.annotation.*;

/**
 * 登录日志注解
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogLogin {

    /**
     * 登录类型
     */
    UcConst.LoginTypeEnum type() default UcConst.LoginTypeEnum.NULL;

}

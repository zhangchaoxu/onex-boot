package com.nb6868.onexboot.api.common.annotation;

import com.nb6868.onexboot.api.modules.uc.UcConst;

import java.lang.annotation.*;

/**
 * 登录日志注解
 *
 * @author Charles zhangchaoxu@gmail.com
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

package com.nb6868.onexboot.api.common.annotation;

import java.lang.annotation.*;

/**
 * 微信页面授权
 * see {https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WxWebAuth {

    /**
     * 授权范围
     */
    String scope() default "snsapi_base";

    /**
     * 约束在微信内
     */
    boolean restrictBrowser() default true;

}

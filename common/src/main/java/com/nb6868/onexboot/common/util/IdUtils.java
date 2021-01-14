package com.nb6868.onexboot.common.util;

import java.util.UUID;

/**
 * ID工具类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class IdUtils {

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}

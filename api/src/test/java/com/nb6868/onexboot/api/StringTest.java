package com.nb6868.onexboot.api;

import org.junit.jupiter.api.Test;

/**
 * 字符串处理
 */
public class StringTest {

    @Test
    void length() {
        String str = "零一二三四五六七八九0123456789!@#$%^&*()";
        System.out.println("str length:" + str.substring(0, 19) + "…");
        //System.out.println("str char length:" + str.substring(0, 20));

    }

}

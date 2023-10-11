package com.nb6868.onex.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.Console;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("验证码测试")
@Slf4j
public class CaptchaTest {

    @Test
    @DisplayName("jsontest")
    void jsonTest() {
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        //图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write("E:/line.png");
        //输出code
        Console.log(lineCaptcha.getCode());
        //验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");
        //重新生成验证码
        lineCaptcha.createCode();
        lineCaptcha.write("E:/line.png");
        //新的验证码
        Console.log(lineCaptcha.getCode());
        //验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");

        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
        //ShearCaptcha captcha = new ShearCaptcha(200, 100, 4, 4);
        //图形验证码写出，可以写出到文件，也可以写出到流
        captcha.write("E:/shear.png");
        //验证图形验证码的有效性，返回boolean值
        captcha.verify("1234");

        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        CircleCaptcha captcha2 = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        //CircleCaptcha captcha = new CircleCaptcha(200, 100, 4, 20);
        captcha2.setGenerator(new RandomGenerator("0123456789", 4));
        captcha2.getCode();
        //图形验证码写出，可以写出到文件，也可以写出到流
        captcha2.write("E:/circle.png");
        captcha2.getImageBase64Data();
        //验证图形验证码的有效性，返回boolean值
        captcha2.verify("1234");
    }

}

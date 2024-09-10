package com.nb6868.onex.uc.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 验证码服务
 * 图形验证码是一个非常低频的功能，缓存部分直接使用hutool的缓存实现
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CaptchaService {

    @Value("${onex.auth.captcha-timeout:900000}")
    private long captchaTimeout;

    // 定时缓存,有效期默认15分钟
    TimedCache<String, String> captchaCache = CacheUtil.newTimedCache(captchaTimeout);

    /**
     * 生成图片验证码
     * 验证码机制是将验证码的内容和对应的uuid的对应关系存入缓存,然后验证的时候从缓存中去匹配
     * uuid不应该由前端生成,否则容易伪造和被攻击
     *
     * @param uuid   uuid
     * @param width  宽度
     * @param height 高度
     * @return 生成的图片
     */
    public String createCaptchaBase64(String uuid, int width, int height) {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(width, height);
        captcha.setGenerator(new RandomGenerator("0123456789", 4));
        captcha.getCode();

        // 保存到缓存
        captchaCache.put(uuid, captcha.getCode().toLowerCase());
        return captcha.getImageBase64Data();
    }

    /**
     * 校验验证码
     *
     * @param uuid uuid
     * @param code 验证码内容
     * @return 验证结果
     */
    public boolean validate(String uuid, String code) {
        // 从缓存获取验证码,不更新时间
        String captcha = captchaCache.get(uuid, false);
        if (StrUtil.isBlank(captcha)) {
            return false;
        }
        // 取出后,从缓存中删除
        captchaCache.remove(uuid);
        return code.equalsIgnoreCase(captcha);
    }

}

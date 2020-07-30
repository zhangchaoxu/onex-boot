package com.nb6868.onex.modules.uc.service.impl;

import com.nb6868.onex.booster.exception.OnexException;
import com.nb6868.onex.modules.uc.service.CaptchaService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码
 * [SpecCaptcha](https://github.com/whvcse/EasyCaptcha)
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    /**
     * 本地缓存
     * 设置一个有效时间10分钟
     */
    Cache<String, String> localCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Override
    public String createBase64(String uuid, int width, int height, String type) {
        Captcha captcha;
        if ("spec".equalsIgnoreCase(type)) {
            // png
            captcha = new SpecCaptcha(width, height);
            captcha.setLen(4);
        } else if ("gif".equalsIgnoreCase(type)) {
            // gif
            captcha = new GifCaptcha(width, height);
        } else if ("chinese".equalsIgnoreCase(type)) {
            // 中文类型
            captcha = new ChineseCaptcha(width, height);
        } else if ("chineseGif".equalsIgnoreCase(type)) {
            // 中文类型
            captcha = new ChineseGifCaptcha(width, height);
        } else if ("arithmetic".equalsIgnoreCase(type)) {
            // 算术
            captcha = new ArithmeticCaptcha(width, height);
        } else {
            throw new OnexException("unknown captcha type");
        }

        // 保存到缓存
        setCache(uuid, captcha.text().toLowerCase());
        return captcha.toBase64();
    }

    /**
     * 校验验证码
     *
     * @param uuid uuid
     * @param code 验证码
     * @return 验证是否成功
     */
    @Override
    public boolean validate(String uuid, String code) {
        // 获取验证码
        String captcha = getCache(uuid);
        // 效验成功
        return code.equalsIgnoreCase(captcha);
    }

    private void setCache(String key, String value) {
        localCache.put(key, value);
    }

    private String getCache(String key) {
        String captcha = localCache.getIfPresent(key);
        // 删除验证码
        if (captcha != null) {
            localCache.invalidate(key);
        }
        return captcha;
    }
}

package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.nb6868.onexboot.api.modules.uc.service.CaptchaService;
import com.nb6868.onexboot.common.exception.OnexException;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 验证码
 * [SpecCaptcha](https://github.com/whvcse/EasyCaptcha)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    Cache captchaCache;

    @Override
    public Captcha createCaptcha(String uuid, int width, int height, String type) {
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
        captchaCache.put(uuid, captcha.text().toLowerCase());
        return captcha;
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
        // 从缓存获取验证码
        String captcha = captchaCache.get(uuid, String.class);
        if (ObjectUtils.isEmpty(captcha)) {
            return false;
        }
        // 取出后,从缓存中删除
        captchaCache.evictIfPresent(uuid);
        return code.equalsIgnoreCase(captcha);
    }

}

package com.nb6868.onex.uc.service;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.OnexException;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

/**
 * 验证码
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class CaptchaService {

    @Autowired
    Cache captchaCache;

    /**
     * 生成图片验证码
     * 验证码机制是将验证码的内容和对应的uuid的对应关系存入缓存,然后验证的时候从缓存中去匹配
     * uuid不应该由前端生成,否则容易伪造和被攻击
     *
     * @param uuid uuid
     * @param width 宽度
     * @param height 高度
     * @param type 类型
     * @return 生成的图片
     */
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
     * @param uuid  uuid
     * @param code  验证码内容
     * @return  验证结果
     */
    public boolean validate(String uuid, String code) {
        // 从缓存获取验证码
        String captcha = captchaCache.get(uuid, String.class);
        if (StrUtil.isBlank(captcha)) {
            return false;
        }
        // 取出后,从缓存中删除
        captchaCache.evictIfPresent(uuid);
        return code.equalsIgnoreCase(captcha);
    }

}

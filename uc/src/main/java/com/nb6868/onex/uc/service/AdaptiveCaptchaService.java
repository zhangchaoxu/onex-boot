package com.nb6868.onex.uc.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 行为验证码服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@ConditionalOnProperty(name = "onex.captcha.adaptive.enable", havingValue = "true")
public class AdaptiveCaptchaService {

    @Value("${onex.captcha.adaptive.timeout:900000}")
    private long captchaTimeout;

    // 定时缓存,有效期默认15分钟
    TimedCache<String, String> captchaCache = CacheUtil.newTimedCache(captchaTimeout);
    // 设置application
    ImageCaptchaApplication application  = TACBuilder.builder()
            .addDefaultTemplate() // 添加默认模板
            // 给滑块验证码 添加背景图片，宽高为600*360, Resource 参数1为 classpath/file/url , 参数2 为具体url
            .addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/1.jpg"))
            .addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "captcha/1.jpg"))
            .addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "captcha/1.jpg"))
            .addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "captcha/1.jpg"))
            .build();

    /**
     * 生成验证码

     * @return 生成的图片base64内容
     */
    public ApiResponse<ImageCaptchaVO> createCaptcha(String captchaType) {
        // 根据验证码类型生成不同的验证码
        return application.generateCaptcha(captchaType);
    }

    /**
     * 校验行为
     *
     * @param id id
     * @param track 行为轨迹
     * @return 验证结果
     */
    public ApiResponse<String> matching(String id, ImageCaptchaTrack track) {
        // 从缓存获取验证码,不更新时间
        ApiResponse<?> res = application.matching(id, new MatchParam(track));
        if (res.isSuccess()) {
            // 将验证码内容保存到缓存
            String value = IdUtil.fastSimpleUUID();
            captchaCache.put(id, value);
            return ApiResponse.ofSuccess(value);
        } else {
            return ApiResponse.of(res.getCode(), res.getMsg(), null);
        }
    }

    /**
     * 校验验证码token
     *
     * @param id id
     * @param code 验证码内容
     * @return 验证结果
     */
    public boolean validate(String id, String code) {
        // 从缓存获取验证码,不更新时间
        String captcha = captchaCache.get(id, false);
        if (StrUtil.isBlank(captcha)) {
            return false;
        }
        // 取出后,从缓存中删除
        captchaCache.remove(id);
        return code.equalsIgnoreCase(captcha);
    }

}

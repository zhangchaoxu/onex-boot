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
    @Value("${onex.auth.captcha-tac:false}")
    private boolean captchaTac;

    // 定时缓存,有效期默认15分钟
    TimedCache<String, String> captchaCache = CacheUtil.newTimedCache(captchaTimeout);

    // TAC相关
    ImageCaptchaApplication tacApplication;

    @PostConstruct
    void init() {
        // 设置application
        if (captchaTac) {
            // 给滑块验证码 添加背景图片，宽高为600*360, Resource 参数1为 classpath/file/url , 参数2 为具体url
            Resource res1 = new Resource("classpath", "tac/bg1.png");
            Resource res2 = new Resource("classpath", "tac/bg2.png");
            Resource res3 = new Resource("classpath", "tac/bg3.png");
            tacApplication = TACBuilder.builder()
                    .addDefaultTemplate() // 添加默认模板
                    .addResource(CaptchaTypeConstant.SLIDER, res1)
                    .addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, res1)
                    .addResource(CaptchaTypeConstant.ROTATE, res1)
                    .addResource(CaptchaTypeConstant.CONCAT, res1)
                    .addResource(CaptchaTypeConstant.SLIDER, res1)
                    .addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, res2)
                    .addResource(CaptchaTypeConstant.ROTATE, res2)
                    .addResource(CaptchaTypeConstant.CONCAT, res2)
                    .addResource(CaptchaTypeConstant.SLIDER, res3)
                    .addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, res3)
                    .addResource(CaptchaTypeConstant.ROTATE, res2)
                    .addResource(CaptchaTypeConstant.CONCAT, res2)
                    .build();
        }
    }

    /**
     * 生成验证码
     *
     * @return 生成的图片base64内容
     */
    public ApiResponse<ImageCaptchaVO> createTACCaptcha(String captchaType) {
        // 根据验证码类型生成不同的验证码
        if (tacApplication == null) {
            return ApiResponse.ofError("TAC未初始化");
        }
        return tacApplication.generateCaptcha(captchaType);
    }

    /**
     * 校验行为
     *
     * @param id    id
     * @param track 行为轨迹
     * @return 验证结果
     */
    public ApiResponse<String> matchingTACCaptcha(String id, ImageCaptchaTrack track) {
        if (tacApplication == null) {
            return ApiResponse.ofError("TAC未初始化");
        }
        // 从缓存获取验证码,不更新时间
        ApiResponse<?> res = tacApplication.matching(id, new MatchParam(track));
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
     * 生成图片验证码
     * 验证码机制是将验证码的内容和对应的uuid的对应关系存入缓存,然后验证的时候从缓存中去匹配
     * uuid不应该由前端生成,否则容易伪造和被攻击
     *
     * @param uuid         uuid
     * @param captchaType  验证码类型，比如circle
     * @param randomBase   随机验证码的文本基础,比如0123456789
     * @param randomLength 随机验证码的长度,建议4或者6
     * @param width        宽度 建议110
     * @param height       高度 建议40
     * @return 生成的图片base64内容
     */
    public String createCaptchaBase64(String uuid, String captchaType, String randomBase, int randomLength, int width, int height) {
        // 根据验证码类型生成不同的验证码
        AbstractCaptcha captcha = switch (captchaType) {
            // 默认circle,所以不做定义
            // case "circle" -> CaptchaUtil.createCircleCaptcha(width, height);
            case "gif" -> CaptchaUtil.createGifCaptcha(width, height);
            case "line" -> CaptchaUtil.createLineCaptcha(width, height);
            case "shear" -> CaptchaUtil.createShearCaptcha(width, height);
            default -> CaptchaUtil.createCircleCaptcha(width, height);
        };
        // 定义随机内容和长度
        captcha.setGenerator(new RandomGenerator(randomBase, randomLength));
        // 将验证码内容保存到缓存
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

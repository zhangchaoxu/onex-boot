package com.nb6868.onex.uc.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

/**
 * TA验证码服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@ConditionalOnClass(name = "cloud.tianai.captcha.application.ImageCaptchaApplication")
public class TACaptchaService {

    // TAC应用
    ImageCaptchaApplication tacApplication;

    @PostConstruct
    void init() {
        // 设置application
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

    /**
     * 生成验证码
     *
     * @return 生成的图片base64内容
     */
    public ApiResult<JSONObject> createCaptcha(String captchaType) {
        // 根据验证码类型生成不同的验证码
        ApiResult<JSONObject> apiResult = ApiResult.of();
        if (tacApplication == null) {
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION).setMsg("TAC未初始化");
        }
        ApiResponse<ImageCaptchaVO> genRes = tacApplication.generateCaptcha(captchaType);
        if (genRes.isSuccess()) {
            return apiResult
                    .setSuccess(true)
                    .setCode(String.valueOf(genRes.getCode()))
                    .setMsg(genRes.getMsg())
                    .setData(JSONUtil.parseObj(genRes.getData()));
        } else {
            return apiResult
                    .setSuccess(false)
                    .setCode(String.valueOf(genRes.getCode()))
                    .setMsg(genRes.getMsg());
        }
    }

    /**
     * 校验行为
     *
     * @param id    id
     * @param trackJson 行为轨迹
     * @return 验证结果
     */
    public ApiResult<?> matching(String id, JSONObject trackJson) {
        ImageCaptchaTrack track = JSONUtil.toBean(trackJson, ImageCaptchaTrack.class);
        ApiResult<?> apiResult = ApiResult.of();
        if (tacApplication == null) {
            return apiResult.error(ApiResult.ERROR_CODE_EXCEPTION).setMsg("TAC未初始化");
        }
        // 从缓存获取验证码,不更新时间
        ApiResponse<?> matchingRes = tacApplication.matching(id, new MatchParam(track));
        if (matchingRes.isSuccess()) {
            return apiResult
                    .setSuccess(true)
                    .setCode(String.valueOf(matchingRes.getCode()))
                    .setMsg(matchingRes.getMsg());
        } else {
            return apiResult
                    .setSuccess(false)
                    .setCode(String.valueOf(matchingRes.getCode()))
                    .setMsg(matchingRes.getMsg());
        }
    }

}

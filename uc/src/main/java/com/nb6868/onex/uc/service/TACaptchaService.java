package com.nb6868.onex.uc.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.common.model.dto.MatchParam;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nb6868.onex.common.pojo.ApiResult;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * TA验证码服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@ConditionalOnClass(name = "cloud.tianai.captcha.application.ImageCaptchaApplication")
public class TACaptchaService {

    @Value("${onex.auth.captcha-ta-bg}")
    private String captchaTABg; // 天爱验证码背景图
    // TAC应用
    ImageCaptchaApplication tacApplication;

    @PostConstruct
    void init() {
        // 从配置文件读取背景图
        List<Resource> resList = new ArrayList<>();
        StrUtil.split(captchaTABg, ",").forEach(path -> {
            // 给滑块验证码 添加背景图片，宽高为600*360, Resource 参数1为 classpath/file/url , 参数2 为具体url
            resList.add(new Resource("classpath", path));
        });
        TACBuilder tacBuilder = TACBuilder.builder()
                .addDefaultTemplate();
        // 添加默认模板
        resList.forEach(resource -> {
            tacBuilder.addResource(CaptchaTypeConstant.SLIDER, resource);
            tacBuilder.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, resource);
            tacBuilder.addResource(CaptchaTypeConstant.ROTATE, resource);
            tacBuilder.addResource(CaptchaTypeConstant.CONCAT, resource);
            tacBuilder.addResource(CaptchaTypeConstant.SLIDER, resource);
        });
        tacApplication = tacBuilder.build();
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
     * @param id        id
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

package com.nb6868.onex.uc.controller;

import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.uc.dto.TianaiCaptchaTrackReq;
import com.nb6868.onex.uc.service.AdaptiveCaptchaService;
import com.nb6868.onex.uc.service.ParamsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController("UcCaptcha")
@RequestMapping("/uc/captcha/")
@Validated
@Tag(name = "验证码")
@Slf4j
public class CaptchaController {

    @Autowired
    ParamsService paramsService;
    @Autowired
    AdaptiveCaptchaService adaptiveCaptchaService;

    @PostMapping("createAdaptiveCaptcha")
    // @AccessControl
    @Operation(summary = "生成行为验证码", description = "Anon")
    public Result<?> createAdaptiveCaptcha(@Validated @RequestBody BaseReq req) {
        // 获得登录验证码配置,设置默认杜绝空信息
        JSONObject captchaParams = paramsService.getSystemPropsObject("ADAPTIVE_CAPTCHA", JSONObject.class, new JSONObject());
        List<String> captchaTypeList = captchaParams.getBeanList("captchaType", String.class);
        captchaTypeList = CollUtil.defaultIfEmpty(captchaTypeList, Arrays.asList(CaptchaTypeConstant.SLIDER, CaptchaTypeConstant.WORD_IMAGE_CLICK));
        ApiResponse<ImageCaptchaVO> res = adaptiveCaptchaService.createCaptcha(RandomUtil.randomEle(captchaTypeList));
        return new Result<>()
                // 按照前端要求成功的时候传回code=200
                .setCode(res.getCode())
                .setMsg(res.getMsg())
                .setData(res.getData());
    }

    @PostMapping("matchAdaptiveCaptcha")
    // @AccessControl
    @Operation(summary = "验证行为验证码", description = "Anon")
    public Result<?> matchAdaptiveCaptcha(@Validated @RequestBody TianaiCaptchaTrackReq req) {
        ApiResponse<?> res = adaptiveCaptchaService.matching(req.getId(), req.getData());
        return new Result<>()
                // 按照前端要求成功的时候传回code=200
                .setCode(res.getCode())
                .setMsg(res.getMsg())
                .setData(res.getData());
    }

}

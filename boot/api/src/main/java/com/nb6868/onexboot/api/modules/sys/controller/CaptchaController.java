package com.nb6868.onexboot.api.modules.sys.controller;

import com.nb6868.onexboot.api.modules.uc.service.CaptchaService;
import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.pojo.Result;
import com.nb6868.onexboot.common.util.StringUtils;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;

/**
 * 验证码接口
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("sys/captcha")
@Validated
@Api(tags = "首页接口")
public class CaptchaController {

    @Autowired
    CaptchaService captchaService;

    /**
     * base64的图形验证码
     * 包含uuid和图片信息
     */
    @GetMapping("base64")
    @ApiOperation(value = "图形验证码(base64格式)")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "int", name = "图片宽度", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "图片高度", required = true),
            @ApiImplicitParam(paramType = "query", dataType="string", name = "uuid")})
    public Result<?> base64(@RequestParam(required = false, defaultValue = "150") int width,
                            @RequestParam(required = false, defaultValue = "50") int height,
                            @RequestParam(required = false) String uuid) {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        // 随机取出一种
        String[] captchaTypes = {"arithmetic", "spec"};
        Captcha captcha = captchaService.createCaptcha(uuid, width, height, captchaTypes[(int) (Math.random() * captchaTypes.length)]);
        // 将uuid和图片的base64返回给前端
        return new Result<>().success(Kv.init().set("uuid", uuid).set("image", captcha.toBase64()));
    }

    /**
     * 流形式的图形验证码
     * uuid由参数决定
     */
    @GetMapping("stream")
    @ApiOperation(value = "图形验证(数据流)", produces="application/octet-stream")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "int", name = "图片宽度", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "图片高度", required = true),
            @ApiImplicitParam(paramType = "query", dataType="string", name = "uuid", required = true)})
    public void captcha(HttpServletResponse response,
                        @RequestParam(required = false, defaultValue = "150") int width,
                        @RequestParam(required = false, defaultValue = "50") int height,
                        @NotNull(message = "uuid不能为空") @RequestParam String uuid) throws IOException {
        // 随机取出一种
        String[] captchaTypes = {"arithmetic", "spec"};
        Captcha captcha = captchaService.createCaptcha(uuid, width, height, captchaTypes[(int) (Math.random() * captchaTypes.length)]);

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        captcha.out(out);
        out.close();
    }

}

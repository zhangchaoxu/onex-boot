package com.nb6868.onex.modules.sys.controller;

import com.nb6868.onex.booster.pojo.Kv;
import com.nb6868.onex.booster.pojo.Result;
import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.modules.uc.service.CaptchaService;
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
    private CaptchaService captchaService;

    @GetMapping("base64")
    @ApiOperation(value = "base64格式验证码图片")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "int", name = "width", value = "图片宽度"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "height", value = "图片高度")})
    @AnonAccess
    public Result<?> base64(@RequestParam(required = false, defaultValue = "150") int width, @RequestParam(required = false, defaultValue = "50") int height) {
        String uuid = UUID.randomUUID().toString();
        // 随机取出一种
        String[] captchaTypes = {"arithmetic", "spec"};
        String image = captchaService.createBase64(uuid, width, height, captchaTypes[(int) (Math.random() * captchaTypes.length)]);
        // 将uuid和图片的base64返回给前端
        return new Result<>().success(Kv.init().set("uuid", uuid).set("image", image));
    }

}

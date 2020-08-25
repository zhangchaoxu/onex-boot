package com.nb6868.onex.modules.uc.controller;

import com.nb6868.onex.common.annotation.AnonAccess;
import com.nb6868.onex.modules.uc.UcConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * html页面
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Controller("UcHtml")
@RequestMapping("uc/html")
@Api(tags = "uc html")
public class HtmlController {

    @AnonAccess
    @ApiOperation("绑定微信")
    @GetMapping("wx/bind")
    public String wxBind(ModelMap map, @RequestParam(required = false, defaultValue = UcConst.WX_CFG_MP) String paramCode, @RequestParam(required = false) String code) {
        return "uc/wx-bind";
    }

}
